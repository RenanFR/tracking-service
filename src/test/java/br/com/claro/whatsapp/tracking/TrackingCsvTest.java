package br.com.claro.whatsapp.tracking;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.claro.whatsapp.tracking.blip.BlipClient;
import br.com.claro.whatsapp.tracking.config.ObjectMapperConfig;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapperImpl;
import br.com.claro.whatsapp.tracking.persistence.entity.GlobalExtrasEntity;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;
import br.com.claro.whatsapp.tracking.resource.TrackingResource;
import br.com.claro.whatsapp.tracking.service.TrackingService;
import br.com.claro.whatsapp.tracking.service.aws.AWSS3Service;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ObjectMapperConfig.class, TrackingMapperImpl.class })
public class TrackingCsvTest {

	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

	@Mock
	private TrackingRepository repository;

	@Mock
	private BlipClient blipClient;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private AWSS3Service s3Service;

	private MockMvc mockMvc;

	private TrackingService service;

	@Autowired
	private TrackingMapper trackingMapper;

	@Before
	public void setup() throws Exception {
		service = new TrackingService(repository, trackingMapper, blipClient, s3Service);
		TrackingResource controller = new TrackingResource(service);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void shouldDownloadTrackingCsv() throws Exception {

		String globalExtrasRaw = "{ \"city\":\"São Paulo\", \"bot-origin\":null, \"campaign-source\":\"site\", \"lastState\":\"planSelectionTVAvailablePlansOptionsOthersUnexpectedInput\", \"main-installation-date\":null, \"userid\":\"b3e73880-594c-4d60-8db1-4f2fd6ca05a2@tunnel.msging.net\", \"full-name\":null, \"alternative-installation-date\":null, \"chosen-product\":\"TV\", \"bank\":null, \"postalcode\":\"04523001\", \"due-date\":null, \"cpf\":\"17438929850\", \"origin-link\":\"https://www.claro.com.br/internet\", \"payment\":null, \"state\":\"SP\", \"api-orders-hash-id\":null, \"email\":null, \"plan-name\":null, \"userphone\":\"86 78403-61  \", \"plan-offer\":null, \"completed-address\":\"04523001 - AV MACUCO, 404 - MOEMA, São Paulo - SP\", \"type-of-person\":\"CPF\", \"type-of-product\":\"Residencial\", \"main-installation-period-day\":null, \"plan-value\":null, \"alternative-installation-period-day\":null }";
		GlobalExtrasEntity globalExtrasEntity = trackingMapper.fromJsonToGlobalExtrasEntity(globalExtrasRaw);

		List<TrackingEntity> trackingList = List
				.of(new TrackingEntity(1L, "claroresidentialsales@msging.net", null, "17867840361@wa.gw.msging.net",
						globalExtrasRaw, "17867840361", "plan-selection tv-available-plans-options-others", null,
						"view", "site", LocalDateTime.now(), globalExtrasEntity));

		LocalDateTime fromDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
		LocalDateTime toDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));

		String from = fromDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
		String to = toDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));

		when(repository.findByRecordDateBetween(fromDate, toDate)).thenReturn(trackingList);

		this.mockMvc.perform(get("/trackings").param("from", from).param("to", to)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType("text/csv"))
				.andExpect(content().string(containsString("claroresidentialsales@msging.net")));
	}

}
