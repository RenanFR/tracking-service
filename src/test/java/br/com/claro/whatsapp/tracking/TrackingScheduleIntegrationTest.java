package br.com.claro.whatsapp.tracking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.s3.AmazonS3;

import br.com.claro.whatsapp.tracking.blip.BlipClient;
import br.com.claro.whatsapp.tracking.config.ObjectMapperConfig;
import br.com.claro.whatsapp.tracking.discord.DiscordTrackingBot;
import br.com.claro.whatsapp.tracking.discord.TrackingSchedule;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapperImpl;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;
import br.com.claro.whatsapp.tracking.service.TrackingService;
import br.com.claro.whatsapp.tracking.service.aws.AWSS3Service;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ObjectMapperConfig.class, TrackingMapperImpl.class })
public class TrackingScheduleIntegrationTest {

	private TrackingSchedule trackingSchedule;

	@Mock
	private TrackingRepository repository;

	@Autowired
	private TrackingMapper trackingMapper;

	@Mock
	private BlipClient blipClient;

	@Mock
	private AWSS3Service s3Service;

	@Mock
	private AmazonS3 s3;

	@Mock
	private DiscordTrackingBot trackingBot;

	@Before
	public void setup() throws Exception {
		trackingSchedule = new TrackingSchedule(new TrackingService(repository, trackingMapper, blipClient, s3Service),
				trackingBot);
	}

	@Test
	public void shouldUploadCsvTrackingToS3AndSendMessageToDiscord()
			throws InterruptedException, IOException, URISyntaxException {

		trackingSchedule.uploadCsvTrackingToS3();

		String trackingCsvKey = "trackings_27-05-2022 00.00_27-05-2022 23.59.csv";

		verify(s3Service, times(1)).upload(eq(trackingCsvKey), any(File.class));
		verify(trackingBot, times(1)).notifyNewTrackingReport(isNull());
	}

}
