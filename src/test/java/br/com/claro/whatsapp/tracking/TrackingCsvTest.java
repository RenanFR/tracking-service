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
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.claro.whatsapp.tracking.mapper.TrackingMapperImpl;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;
import br.com.claro.whatsapp.tracking.resource.TrackingResource;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@RunWith(MockitoJUnitRunner.class)
public class TrackingCsvTest {

	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

	@Mock
	private TrackingRepository repository;
	
	private MockMvc mockMvc;
	
	private TrackingService service;
	
    @Before
    public void setup() throws Exception {
    	service = new TrackingService(repository, new TrackingMapperImpl());
    	TrackingResource controller = new TrackingResource(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void shouldDownloadTrackingCsv() throws Exception {
		
		List<TrackingEntity> trackingList = List.of(new TrackingEntity(1L, "claroresidentialsales@msging.net", "17867840361@wa.gw.msging.net", "{ &quot;city&quot;:&quot;São Paulo&quot;, &quot;bot-origin&quot;:null, &quot;campaign-source&quot;:&quot;site&quot;, &quot;lastState&quot;:&quot;planSelectionTVAvailablePlansOptionsOthersUnexpectedInput&quot;, &quot;main-installation-date&quot;:null, &quot;userid&quot;:&quot;b3e73880-594c-4d60-8db1-4f2fd6ca05a2@tunnel.msging.net&quot;, &quot;full-name&quot;:null, &quot;alternative-installation-date&quot;:null, &quot;chosen-product&quot;:&quot;TV&quot;, &quot;bank&quot;:null, &quot;postalcode&quot;:&quot;04523001&quot;, &quot;due-date&quot;:null, &quot;cpf&quot;:&quot;17438929850&quot;, &quot;origin-link&quot;:&quot;https://www.claro.com.br/internet&quot;, &quot;payment&quot;:null, &quot;state&quot;:&quot;SP&quot;, &quot;api-orders-hash-id&quot;:null, &quot;email&quot;:null, &quot;plan-name&quot;:null, &quot;userphone&quot;:&quot;86 78403-61  &quot;, &quot;plan-offer&quot;:null, &quot;completed-address&quot;:&quot;04523001 - AV MACUCO, 404 - MOEMA, São Paulo - SP&quot;, &quot;type-of-person&quot;:&quot;CPF&quot;, &quot;type-of-product&quot;:&quot;Residencial&quot;, &quot;main-installation-period-day&quot;:null, &quot;plan-value&quot;:null, &quot;alternative-installation-period-day&quot;:null }", "17867840361", "plan-selection tv-available-plans-options-others view", "site", LocalDateTime.now()));
		
		LocalDateTime fromDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
		LocalDateTime toDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));
		
		String from = fromDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
		String to = toDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
		
		when(repository.findByRecordDateBetween(fromDate, toDate)).thenReturn(trackingList);
		
		this.mockMvc.perform(get("/trackings")
				.param("from", from)
				.param("to", to))
						.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(containsString("claroresidentialsales@msging.net")));
	}

}
