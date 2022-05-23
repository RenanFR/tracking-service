package br.com.claro.whatsapp.tracking.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.claro.whatsapp.tracking.mapper.GlobalExtrasDeserializer;

public record Tracking(Long id, String chatbotId, String sessionId, String originalUserId, String globalExtrasRaw,
		String userPhone, String type, String suffix, String category, String campaignSource, LocalDateTime recordDate,
		@JsonDeserialize(using = GlobalExtrasDeserializer.class) GlobalExtras globalExtras) {

}
