package br.com.claro.whatsapp.tracking.service;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

import br.com.claro.whatsapp.tracking.blip.BlipClient;
import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.model.BlipResponse;
import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;

@Service
public class TrackingService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingService.class);

	private TrackingRepository repository;

	private TrackingMapper mapper;
	
	private BlipClient blipClient;
	
	private AWSS3Service s3Service;
	
	@Autowired
	public TrackingService(TrackingRepository repository, TrackingMapper mapper, BlipClient blipClient, AWSS3Service s3Service) {
		this.repository = repository;
		this.mapper = mapper;
		this.blipClient = blipClient;
		this.s3Service = s3Service;
	}

	public List<Tracking> fetchFromPeriod(PrintWriter writer, LocalDateTime from, LocalDateTime to) {
		List<Tracking> trackingList = fetchFromPeriod(from, to);

		generateTrackingCsv(writer, trackingList);
		return trackingList;

	}

	public List<Tracking> fetchFromPeriod(LocalDateTime from, LocalDateTime to) {
		List<TrackingEntity> entities = repository.findByRecordDateBetween(from, to);
		List<Tracking> trackingList = entities.stream().map(mapper::entityToRecord).collect(Collectors.toList());
		return trackingList;
	}

	public void generateTrackingCsv(Writer writer, List<Tracking> trackingList) {
		try {
			
			ColumnPositionMappingStrategy<Tracking> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(Tracking.class);
			String[] columns = new String[] { "chatbotId", "originalUserId", "globalExtrasRaw", "userPhone", "campaignSource",
					"category", "recordDate" };
			mappingStrategy.setColumnMapping(columns);
			StatefulBeanToCsv<Tracking> toCsv = new StatefulBeanToCsvBuilder<Tracking>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withMappingStrategy(mappingStrategy).withSeparator(',')
					.build();
			toCsv.write(trackingList);
			
		} catch (CsvException csvException) {

            LOGGER.error(csvException.getMessage());
        }
	}

	public Tracking recordNewTrackingEntry(Tracking tracking) {
		TrackingEntity entity = mapper.recordToEntity(tracking);
		TrackingEntity savedTracking = repository.save(entity);
		return mapper.entityToRecord(savedTracking);
	}
	
	public BlipResponse sendTrackingToBlip(String request) {
		BlipResponse response = blipClient.postTracking(request);
		try {
			String writeValueAsString = new ObjectMapper().writeValueAsString(response);
			request.chars();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	public void uploadTrackingCsv(String trackingCsvKey, File trackingFile) {
		s3Service.upload(trackingCsvKey, trackingFile);
		
	}

}
