package br.com.claro.whatsapp.tracking.model;

import java.time.LocalDateTime;

public record Tracking(Long id, String chatbotId, String originalUserId, String globalExtras, String userPhone, String category, String campaignSource, LocalDateTime recordDate) {

}
