package br.com.claro.whatsapp.tracking.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@Component
public class TrackingListener {
	
	@Autowired
  	private TrackingService service;
	
	@SqsListener("tracking-queue")
	public void receive(Tracking tracking) {
		service.recordNewTrackingEntry(tracking);
	}

}
