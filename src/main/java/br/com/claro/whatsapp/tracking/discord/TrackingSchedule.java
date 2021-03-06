package br.com.claro.whatsapp.tracking.discord;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.claro.whatsapp.tracking.service.TrackingService;

@EnableAsync
@Component
public class TrackingSchedule {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingSchedule.class);

	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH.mm";

	private TrackingService service;
	private DiscordTrackingBot trackingBot;

	@Autowired
	public TrackingSchedule(TrackingService service, DiscordTrackingBot trackingBot) {
		this.service = service;
		this.trackingBot = trackingBot;
	}

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

		String presignedForUrlTrackingCsv = service.fetchTrackingsAndUploadFileToS3(startOfTheDay, endOfTheDay, trackingCsvKey);
		trackingBot.notifyNewTrackingReport(presignedForUrlTrackingCsv);

	}



}
