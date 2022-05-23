package br.com.claro.whatsapp.tracking.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@Controller
public class TrackingListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingListener.class);
	
	@Autowired
  	private TrackingService service;
	
	@SqsListener(value = "http://localhost:4566/000000000000/tracking-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receive(Tracking tracking, 
		    @Header("SenderId") String senderId) {
		LOGGER.info("Evento de tracking recebido com payload: {} e Id de envio: {}", tracking, senderId);
		service.recordNewTrackingEntry(tracking);
	}

}
