package br.com.claro.whatsapp.tracking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeignErrorDecoder.class);

	@Override
	public Exception decode(String methodKey, Response response) {
		LOGGER.error("ERRO {} NA CHAMADA COM A BLIP PARA INTEGRACAO DOS TRACKINGS: {}, METODO: {}", response.status(),
				response.reason(), methodKey);
		return new TrackingException(response.reason());
	}

}
