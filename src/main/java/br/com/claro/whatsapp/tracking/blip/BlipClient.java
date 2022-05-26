package br.com.claro.whatsapp.tracking.blip;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.claro.whatsapp.tracking.model.BlipResponse;

@FeignClient(value = "blip", url = "https://claro.http.msging.net")
public interface BlipClient {

	@RequestMapping(method = RequestMethod.POST, value = "/commands")
	BlipResponse postTracking(String request);

}
