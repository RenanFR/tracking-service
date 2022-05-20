package br.com.claro.whatsapp.tracking.resource;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.service.TrackingService;

@RestController
@RequestMapping("trackings")
public class TrackingResource {
	
	@Autowired
  	private TrackingService service;
	
    @GetMapping(produces = "text/csv")
    public void fetchFromPeriod(@RequestParam("from") LocalDateTime from, @RequestParam("to") LocalDateTime to, 
    		HttpServletResponse response) throws IOException {
    	service.fetchFromPeriod(response.getWriter(), from, to);
    }
    
    @PostMapping
    public void recordNewTrackingEntry(@RequestBody Tracking tracking) {
    	service.recordNewTrackingEntry(tracking);
	}
    
}
