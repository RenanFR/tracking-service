package br.com.claro.whatsapp.tracking;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import br.com.claro.whatsapp.tracking.blip.BlipClient;
import br.com.claro.whatsapp.tracking.config.CloudConfig;
import br.com.claro.whatsapp.tracking.config.FeignConfig;
import br.com.claro.whatsapp.tracking.config.ObjectMapperConfig;
import br.com.claro.whatsapp.tracking.exception.FeignErrorDecoder;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapperImpl;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;
import br.com.claro.whatsapp.tracking.service.AWSS3Service;
import br.com.claro.whatsapp.tracking.service.TrackingService;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { ObjectMapperConfig.class, TrackingMapperImpl.class, CloudConfig.class, FeignConfig.class,
		FeignErrorDecoder.class })
public class TrackingServiceTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
			options().port(2345).extensions(new ResponseTemplateTransformer(false)).notifier(new Slf4jNotifier(false)));
	
    @Before
    public void setUp() {
    	stubFor(post(urlEqualTo("/commands"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("sample-blip-response.json")
                ));
    }

	private TrackingService service;

	@Mock
	private TrackingRepository repository;

	@Autowired
	private TrackingMapper trackingMapper;

	@Autowired
	private BlipClient blipClient;

	@Mock
	private AWSS3Service s3Service;

	@Before
	public void setup() throws Exception {
		service = new TrackingService(repository, trackingMapper, blipClient, s3Service);
	}

	@Test
	public void shouldIntegrateTrackingWithBlip() throws Exception {
		String request = new String(Files.readAllBytes(Paths.get("src/test/resources/sample-blip-request.json")));
		service.sendTrackingToBlip(request);
	}

}
