package br.com.claro.whatsapp.tracking.model;

public record BlipResponse(String method, String status, String id, String from, String to, BlipResponseMetadata metadata) {
	
}
