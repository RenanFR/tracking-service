package br.com.claro.whatsapp.tracking.resource;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@RestController
@RequestMapping(path = { "trackings" })
public class TrackingResource {
	
	private static final String CONTENT_TYPE_TEXT_CSV = "text/csv";
	private static final String CONTENT_TYPE_JSON = "application/json";
	
  	private TrackingService service;
  	
  	@Autowired
  	public TrackingResource(TrackingService service) {
  		this.service = service;
	}
	
    @GetMapping(produces = CONTENT_TYPE_TEXT_CSV)
    public ResponseEntity<Void> fetchFromPeriod(@RequestParam("from") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime from, 
    		@RequestParam("to") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime to, 
    		HttpServletResponse response) throws IOException {
    	response.setContentType(CONTENT_TYPE_TEXT_CSV);
    	service.fetchFromPeriod(response.getWriter(), from, to);
		return ResponseEntity.ok().build();
    }
    
    @PostMapping(produces = CONTENT_TYPE_JSON)
    public ResponseEntity<Tracking> recordNewTrackingEntry(@RequestBody Tracking tracking) {
    	Tracking trackingEntry = service.recordNewTrackingEntry(tracking);
		return ResponseEntity.status(HttpStatus.CREATED).body(trackingEntry);
	}
    
}
