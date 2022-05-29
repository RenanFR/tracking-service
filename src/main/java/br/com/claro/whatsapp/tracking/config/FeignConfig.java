package br.com.claro.whatsapp.tracking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
@EnableFeignClients({ "br.com.claro.whatsapp.tracking.blip" })
@ImportAutoConfiguration({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
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
