package br.com.claro.whatsapp.tracking.service;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

import br.com.claro.whatsapp.tracking.mapper.TrackingMapper;
import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;
import br.com.claro.whatsapp.tracking.persistence.repository.TrackingRepository;

@Service
public class TrackingService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingService.class);

	@Autowired
	private TrackingRepository repository;

	@Autowired
	private TrackingMapper mapper;

	public void fetchFromPeriod(PrintWriter writer, LocalDateTime from, LocalDateTime to) {
		List<TrackingEntity> entities = repository.findByRecordDateBetween(from, to);
		List<Tracking> trackingList = entities.stream().map(mapper::entityToRecord).collect(Collectors.toList());

		try {
			
			ColumnPositionMappingStrategy<Tracking> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(Tracking.class);
			String[] columns = new String[] { "chatbotId", "originalUserId", "globalExtras", "userPhone", "campaignSource",
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
	
	public void recordNewTrackingEntry(Tracking tracking) {
		TrackingEntity entity = mapper.recordToEntity(tracking);
		repository.save(entity);
	}

}
