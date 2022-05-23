package br.com.claro.whatsapp.tracking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.claro.whatsapp.tracking.config.ObjectMapperConfig;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapperImpl;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;
import br.com.claro.whatsapp.tracking.resource.TrackingResource;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectMapperConfig.class, TrackingMapperImpl.class})
public class TrackingPersistenceTest {

	@Mock
	private TrackingRepository repository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TrackingMapper trackingMapper;
	
	private MockMvc mockMvc;
	
	private TrackingService service;
	
    @Before
    public void setup() throws Exception {
    	service = new TrackingService(repository, trackingMapper);
    	TrackingResource controller = new TrackingResource(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void shouldDownloadTrackingCsv() throws Exception {
		String json = new String(Files.readAllBytes(Paths.get("src/test/resources/sample-tracking-record.json")));
		
		TrackingEntity entity = objectMapper.readValue(json, TrackingEntity.class);
		
		when(repository.save(any(TrackingEntity.class))).thenReturn(entity);
		
		this.mockMvc.perform(post("/trackings").contentType(MediaType.APPLICATION_JSON).content(json))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.chatbotId").value("claroresidentialsales@msging.net"));
	}

}
