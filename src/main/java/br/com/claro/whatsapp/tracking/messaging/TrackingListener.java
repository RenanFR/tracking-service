package br.com.claro.whatsapp.tracking.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;

import br.com.claro.whatsapp.tracking.model.BlipResponse;
import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@Controller
public class TrackingListener {

	private static final String TRACKING_QUEUE_BLIP = "http://localhost:4566/000000000000/tracking-queue-blip";

	private static final String TRACKING_QUEUE_PERSISTENCE = "http://localhost:4566/000000000000/tracking-queue-persistence";

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackingListener.class);

	@Autowired
	private TrackingService service;

	@SqsListener(value = TRACKING_QUEUE_PERSISTENCE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveAndPersist(Tracking tracking, @Header("SenderId") String senderId) {
		LOGGER.info("EVENTO DE TRACKING RECEBIDO PARA PERSISTENCIA COM PAYLOAD: {} E ID DE ENVIO: {}", tracking,
				senderId);
		service.recordNewTrackingEntry(tracking);
	}

	@SqsListener(value = TRACKING_QUEUE_BLIP, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveAndSendToBlip(String tracking, @Header("SenderId") String senderId) {
		LOGGER.info("TRACKING RECEBIDO PARA INTEGRACAO COM A BLIP: {} E ID DE ENVIO: {}", tracking, senderId);
		BlipResponse blipResponse = service.sendTrackingToBlip(tracking);
		LOGGER.info("TRACKING INTEGRADO COM SUCESSO, RESPOSTA DA BLIP: {}", blipResponse.status());
	}

}
