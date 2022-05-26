package br.com.claro.whatsapp.tracking.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@EnableAsync
@Component
public class TrackingSchedule {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingSchedule.class);

	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

	private TrackingService service;

	@Autowired
	public TrackingSchedule(TrackingService service) {
		this.service = service;
	}

	@Scheduled(fixedRate = 1800000)
	private void uploadCsvTrackingToS3() throws InterruptedException, IOException, URISyntaxException {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime until = now.minusMinutes(30);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

		String nowString = now.format(dateTimeFormatter);
		String untilString = until.format(dateTimeFormatter);

		LOGGER.info("ROTINA DE GERACAO DO CSV DE TRACKING DO PERIODO: {} ATE: {}", untilString, nowString);

		String trackingCsvKey = "trackings_" + nowString + "_" + untilString + ".csv";

		List<Tracking> lastHalfHourTrackings = service.fetchFromPeriod(until, now);

		Path path = Paths.get(ClassLoader.getSystemResource(trackingCsvKey).toURI());

		service.generateTrackingCsv(new FileWriter(path.toString()), lastHalfHourTrackings);

		File file = new ClassPathResource("classpath:/" + trackingCsvKey).getFile();

		service.uploadTrackingCsv(trackingCsvKey, file);

	}

}
