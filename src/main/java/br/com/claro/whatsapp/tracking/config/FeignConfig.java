package br.com.claro.whatsapp.tracking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {
	
	private static final String CONTENT_TYPE = "application/json";
	
	@Value("${blip.key}")
	private String blipKey;
	
	@Bean
	public RequestInterceptor requestInterceptor() {
	  return requestTemplate -> {
	      requestTemplate.header("Content-Type", CONTENT_TYPE);
	      requestTemplate.header("Authorization", blipKey);
	  };
	}

}
