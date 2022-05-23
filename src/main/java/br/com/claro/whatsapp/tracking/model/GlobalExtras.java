package br.com.claro.whatsapp.tracking.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GlobalExtras(Long id, String city, @JsonProperty("bot-origin") String botOrigin,
		@JsonProperty("campaign-source") String campaignSource, String lastState,
		@JsonProperty("main-installation-date") LocalDateTime mainInstallationDate,
		@JsonProperty("userid") String userId, @JsonProperty("full-name") String fullName,
		@JsonProperty("alternative-installation-date") LocalDateTime alternativeInstallationDate,
		@JsonProperty("chosen-product") String chosenProduct, String bank,
		@JsonProperty("postalcode") String postalCode, @JsonProperty("due-date") LocalDateTime dueDate, String cpf,
		@JsonProperty("origin-link") String originLink, String payment, String state,
		@JsonProperty("api-orders-hash-id") String apiOrdersHashId, String email,
		@JsonProperty("api-orders-error") String apiOrdersError, @JsonProperty("plan-name") String planName,
		@JsonProperty("userphone") String userPhone, @JsonProperty("plan-offer") String planOffer,
		@JsonProperty("completed-address") String completedAddress, @JsonProperty("type-of-person") String typeOfPerson,
		@JsonProperty("onboarding-simplified") String onboardingSimplified,
		@JsonProperty("type-of-product") String typeOfProduct,
		@JsonProperty("main-installation-period-day") String mainInstallationPeriodDay,
		@JsonProperty("plan-value") String planValue,
		@JsonProperty("alternative-installation-period-day") String alternativeInstallationPeriodDay,
		Tracking tracking) {

}
