# Tracking Service
Serviço para gravação e emissão de Trackings para a plataforma blip por parte das aplicações de chatbot com whatsapp
Consiste em um Microsserviço que irá fazer o recebimento e tratamento dos eventos de emissão de trackings por parte do chatbot do whatsapp. Ele tem como responsabilidades
- Gravar os trackings na base de dados interna para consulta das áreas de QA e CSM tirando assim a dependência do relatório de dia seguinte da Take Blip
- Realizar a chamada de API de integração dos trackings para a Take Blip tirando essa responsabilidade do chatbot residencial
  - A classe TrackingService no chatbot-sales ao invés de chamar o endpoint da Blip diretamente apenas irá repassar o request para o serviço via Mensageria
```java
	@Override
	protected void consume(TrackingRequest request) {
		try {
			String jsonTracking = new ObjectMapper().writeValueAsString(request);
			messagingTemplate.convertAndSend(BLIP_TRACKING_QUEUE, jsonTracking);
		} catch (JsonProcessingException e) {
			BotLogger.errorStack(null, null, null, e);
		}
	}
```
No aspecto técnico é uma aplicação Spring Boot contendo os seguintes componentes
## Banco de dados
- Base de dados relacional com as entidades Tracking e GlobalExtras
  - A primeira representa o evento em si contendo o id do Tracking conforme está no figma e no descriptor
  - A segunda diz respeito ao json de extras globais que é enviado junto ao  Tracking e contém dados obtidos durante a navegação do usuário no bot como é o caso de sua origem e o email por exemplo
- Integração com o liquibase que é uma ferramenta para a gestão de alterações na base de dados
  - A configuração se dará pelo arquivo liquibase.properties que por sua vez aponta a localização do changeLog que é o xml indicando a estrutura inicial da database bem como a localização dos demais changeSets a serem executados
  - Os changeSets são arquivos xml indicando operações de migração no db a serem executados conforme a sintaxe do liquibase
## Chamadas http com Feign
- Utilização do Feign para implementar o Client que fará a chamada de API da Take para envio dos trackings
  - Consiste na criação da interface com a url do bot na Take bem como o método para a chamada POST para o endpoint commands
  - No pacote de configuração haverá uma classe do tipo @Configuration indicando os packages onde estarão os Clients e também um @Bean do tipo RequestInterceptor para autenticar a chamada do endpoint passando a key que obtemos na página da Blip para o nosso bot
  - Por fim temos um ErrorDecoder para separar de forma global o tratamento de Exception lançada pelo Client Feign no caso de por exemplo um Bad Request
```java
@FeignClient(value = "blip", url = "${blip.url}")
public interface BlipClient {

	@RequestMapping(method = RequestMethod.POST, value = "/commands")
	BlipResponse postTracking(String request);

}
```
```java
@Configuration
@EnableFeignClients({ "br.com.claro.whatsapp.tracking.blip" })
@ImportAutoConfiguration({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
public class FeignConfig {
	
	private static final String CONTENT_TYPE = "application/json";
	
	@Value("${blip.key}")
	private String blipKey;
	
	@Bean
	public RequestInterceptor requestInterceptor() {
	  return requestTemplate -> {
	      requestTemplate.header("Content-Type", CONTENT_TYPE);
	      requestTemplate.header("Authorization", blipKey);
	  };
	}

}
```
```java
@Component
public class FeignErrorDecoder implements ErrorDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeignErrorDecoder.class);

	@Override
	public Exception decode(String methodKey, Response response) {
		LOGGER.error("ERRO {} NA CHAMADA COM A BLIP PARA INTEGRACAO DOS TRACKINGS: {}, METODO: {}", response.status(),
				response.reason(), methodKey);
		return new TrackingException(response.reason());
	}

}
```
## Mapeamento de DTOs
O projeto utiliza o padrão mais recente do java para construção de DTOs ou POJO por meio de records e para mapeamento entre eles e nossas classes de entidade usamos o mapstruct
- Utilização do mapstruct para mapeamento de DTOs
    - Através da criação de uma classe abstrata ou interface definimos na assinatura do método os tipos de DTO origem e alvo e o plugin no pom automaticamente gera a implementação do código de mapeamento
```java
@Mapper(componentModel = "spring")
public abstract class TrackingMapper {

	private ObjectMapper customObjectMapper;

	@Mapping(source = "globalExtrasRaw", target = "globalExtras", qualifiedByName = "fromJsonToGlobalExtras")
	public abstract Tracking entityToRecord(TrackingEntity tracking);

	@Mapping(source = "globalExtrasRaw", target = "globalExtras", qualifiedByName = "fromJsonToGlobalExtrasEntity")
	public abstract TrackingEntity recordToEntity(Tracking tracking);

	@Named("fromJsonToGlobalExtras")
	public GlobalExtras fromJsonToGlobalExtras(String jsonExtras) throws JsonProcessingException {
		if (jsonExtras == null) {
			return null;
		}
		String unescapedJson = StringEscapeUtils.unescapeJson(jsonExtras).replaceAll("^\"|\"$", "");
		GlobalExtras extras = customObjectMapper.readValue(unescapedJson, GlobalExtras.class);
		return extras;
	}

	@Named("fromJsonToGlobalExtrasEntity")
	public GlobalExtrasEntity fromJsonToGlobalExtrasEntity(String jsonExtras) throws JsonProcessingException {
		if (jsonExtras == null) {
			return null;
		}
		String unescapedJson = StringEscapeUtils.unescapeJson(jsonExtras).replaceAll("^\"|\"$", "");
		GlobalExtrasEntity extras = customObjectMapper.readValue(unescapedJson, GlobalExtrasEntity.class);
		return extras;
	}

	@Autowired
	public void setCustomObjectMapper(ObjectMapper customObjectMapper) {
		this.customObjectMapper = customObjectMapper;
	}

}
```
## Mock de API para teste com wiremock
- Utilização do wiremock para respostas estáticas de API no contexto de testes do projeto
    - Consiste na dependência no pom bem como da cláusula @Rule na classe de teste onde indicamos a porta onde o wiremock irá responder
    - Por meio do método stubFor fazendo o match da assinatura do endpoint com o Mock estático de resposta que por padrão se encontra no diretório __files nos resources de teste
    - Quando a chamada for executada no contexto do @Test ao invés de chamar a API real será usado o Mock
```java
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
			options().port(2345).extensions(new ResponseTemplateTransformer(false)).notifier(new Slf4jNotifier(false)));

	@Before
	public void setUp() {
		stubFor(post(urlEqualTo("/commands")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBodyFile("sample-blip-response.json")));
	}
	
	@Test
	public void shouldIntegrateTrackingWithBlip() throws Exception {
		String request = new String(Files.readAllBytes(Paths.get("src/test/resources/sample-blip-request.json")));
		BlipResponse blipResponse = service.sendTrackingToBlip(request);
		assertEquals(blipResponse.status(), "success");
	}
```
## Mensageria na amazon com Sqs
- Dois métodos Listener para as filas do Sqs (Uma para a persistência e a segunda para emissão junto a Take)
    - Anotados com @SqsListener o primeiro fará a chamada do repository para persistência no db e o segundo fará a chamada do FeignClient para integrar o tracking com a Take
    - A configuração de conexão com as Queues se dará por meio de uma @Configuration
```java
	@SqsListener(value = TRACKING_QUEUE_PERSISTENCE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveAndPersist(Tracking tracking, @Header("SenderId") String senderId) {
		LOGGER.info("EVENTO DE TRACKING RECEBIDO PARA PERSISTENCIA COM PAYLOAD: {} E ID DE ENVIO: {}", tracking,
				senderId);
		service.recordNewTrackingEntry(tracking);
	}

	@SqsListener(value = TRACKING_QUEUE_BLIP, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveAndSendToBlip(String tracking, @Header("SenderId") String senderId) {
		LOGGER.info("TRACKING RECEBIDO PARA INTEGRACAO COM A BLIP: {} E ID DE ENVIO: {}", tracking, senderId);
		BlipResponse blipResponse = service.sendTrackingToBlip(tracking);
		LOGGER.info("TRACKING INTEGRADO COM SUCESSO, RESPOSTA DA BLIP: {}", blipResponse.status());
	}
```
## Processamento batch
- Teremos uma rotina periódica @Scheduled (Um mini processamento batch) para a consolidação dos trackings do dia, essa rotina irá executar diariamente recuperando os trackings do dia e enviando o csv correspondente ao S3 onde teremos um bucket dedicado
    - Após o upload no bucket a rotina irá gerar uma Url provisória para o S3 onde os times poderão acessar o arquivo referente ao dia anterior por um período limitado de tempo e após isso os csvs antigos permanecerão no bucket para fins de backup
    - A rotina irá também notificar via Discord em um canal específico o time de QA e CSM por meio de uma mensagem contendo a Url do último csv gerado no S3
```java
	@Scheduled(fixedRate = 1800000)
	public void uploadCsvTrackingToS3() throws InterruptedException, IOException, URISyntaxException {
		LocalDate date = LocalDate.of(2022, 5, 27);

		LocalDateTime startOfTheDay = LocalDateTime.of(date, LocalTime.of(0, 0));
		LocalDateTime endOfTheDay = LocalDateTime.of(date, LocalTime.of(23, 59));

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

		String from = startOfTheDay.format(dateTimeFormatter);
		String to = endOfTheDay.format(dateTimeFormatter);

		LOGGER.info("ROTINA DE GERACAO DO CSV DE TRACKING NO PERIODO DE: {} ATE: {}", from, to);

		String trackingCsvKey = "trackings_" + from + "_" + to;

		List<Tracking> todayTrackings = service.fetchFromPeriod(startOfTheDay, endOfTheDay);

		File file = Files.createTempFile(trackingCsvKey, ".csv").toFile();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

			service.generateTrackingCsv(writer, todayTrackings);

			String fileName = trackingCsvKey + ".csv";

			service.uploadTrackingCsv(fileName, file);
			
			String presignedForUrlTrackingCsv = service.getPresignedForUrlTrackingCsv(fileName);
			trackingBot.notifyNewTrackingReport(presignedForUrlTrackingCsv);
		}

	}
```
## Integração com Discord
Para essa integração usaremos a biblioteca discord4j que encapsula a interação com a API do Discord
- Funciona da seguinte maneira
    - No portal de developer do Discord criamos uma aplicação
    - Dentro da aplicação criamos um Bot
    - Na aba oauth2 obteremos um token para interação programática com o Bot
    - No spring criamos uma @Configuration para criar o @Bean do gateway de comunicação com o qual faremos chamadas para enviar a mensagem para o canal no Discord
    - Com o channelId em mãos criamos uma @Service que usa o gateway previamente configurado e o sdk do  discord4j para disparar uma mensagem imperativa ao canal contendo a Url do csv
```java
@Configuration
public class DiscordTrackingBotConfig {

	@Value("${discord.token}")
	private String discordToken;

	@Bean
	public GatewayDiscordClient gatewayDiscordClient() {
		return DiscordClientBuilder.create(discordToken).build().login().block();
	}

}
```
```java
@Service
public class DiscordTrackingBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiscordTrackingBot.class);

	private GatewayDiscordClient client;

	@Value("${discord.channelId}")
	private String channelId;

	@Autowired
	public DiscordTrackingBot(GatewayDiscordClient client) {
		this.client = client;
	}

	public MessageData notifyNewTrackingReport(String fileS3Url) throws IOException {

		MessageCreateSpec messageWithFile = MessageCreateSpec.builder()
				.content("O relatório diário de Tracking já está disponível em: " + fileS3Url).build();

		MessageData blockMessageData = client.rest().getChannelById(Snowflake.of(channelId))
				.createMessage(messageWithFile.asRequest().getJsonPayload()).block();
		return blockMessageData;
	}

}
```
## Testando interação com a aws localmente
Para testes locais envolvendo o código relacionado a interação com a aws utilizamos o localstack
- Consiste em um Docker que após inicializado provê uma infraestrutura aws local com a qual podemos interagir via comandos awslocal

Estando na raiz do projeto execute
```sh
docker-compose up
```
Em seguida
```sh
awslocal sqs create-queue --queue-name tracking-queue-blip
awslocal sqs create-queue --queue-name tracking-queue-persistence
awslocal s3api create-bucket --bucket tracking-csv-bucket --create-bucket-configuration LocationConstraint=eu-central-1
```
## Referência das ferramentas utilizadas

 Link |
 ------ |
 [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/) |
 [MapStruct](https://www.baeldung.com/mapstruct) |
 [WireMock](https://wiremock.org/docs/stubbing/) |
 [LocalStack](https://auth0.com/blog/spring-cloud-messaging-with-aws-and-localstack/) |
 [Discord4J](https://discord4j.com/) |
