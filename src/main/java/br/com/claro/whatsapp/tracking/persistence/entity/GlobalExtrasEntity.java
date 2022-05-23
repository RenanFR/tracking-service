package br.com.claro.whatsapp.tracking.persistence.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="global_extras")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalExtrasEntity {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String city;
    
    @JsonProperty("bot-origin")
    @Column(name="bot_origin")
    private String botOrigin;
    
    @JsonProperty("campaign-source")
    @Column(name="campaign_source")
    private String campaignSource;
    
    @Column(name="last_state")
    private String lastState;
    
    @JsonProperty("main-installation-date")
    @Column(name="main_installation_date")
    private LocalDateTime mainInstallationDate;
    
    @JsonProperty("userid")
    @Column(name="user_id")
    private String userId;
    
    @JsonProperty("full-name")
    @Column(name="full_name")
    private String fullName;
    
    @JsonProperty("alternative-installation-date")
    @Column(name="alternative_installation_date")
    private LocalDateTime alternativeInstallationDate;
    
    @JsonProperty("chosen-product")
    @Column(name="chosen_product")
    private String chosenProduct;
    
    @Column
    private String bank;
    
    @JsonProperty("postalcode")
    @Column(name="postal_code")
    private String postalCode;
    
    @JsonProperty("due-date")
    @Column(name="due_date")
    private LocalDateTime dueDate;
    
    @Column
    private String cpf;
    
    @JsonProperty("origin-link")
    @Column(name="origin_link")
    private String originLink;
    
    @Column
    private String payment;
    
    @Column
    private String state;
    
    @JsonProperty("api-orders-hash-id")
    @Column(name="api_orders_hash_id")
    private String apiOrdersHashId;
    
    @Column
    private String email;
    
    @JsonProperty("api-orders-error")
    @Column(name="api_orders_error")
    private String apiOrdersError;
    
    @JsonProperty("plan-name")
    @Column(name="plan_name")
    private String planName;
    
    @JsonProperty("userphone")
    @Column(name="user_phone")
    private String userPhone;
    
    @JsonProperty("plan-offer")
    @Column(name="plan_offer")
    private String planOffer;
    
    @JsonProperty("completed-address")
    @Column(name="completed_address")
    private String completedAddress;
    
    @JsonProperty("type-of-person")
    @Column(name="type_of_person")
    private String typeOfPerson;
    
    @JsonProperty("onboarding-simplified")
    @Column(name="onboarding_simplified")
    private String onboardingSimplified;
    
    @JsonProperty("type-of-product")
    @Column(name="type_of_product")
    private String typeOfProduct;
    
    @JsonProperty("main-installation-period-day")
    @Column(name="main_installation_period_day")
    private String mainInstallationPeriodDay;
    
    @JsonProperty("plan-value")
    @Column(name="plan_value")
    private String planValue;
    
    @JsonProperty("alternative-installation-period-day")
    @Column(name="alternative_installation_period_day")
    private String alternativeInstallationPeriodDay;
    
    @OneToOne(mappedBy = "globalExtras")
    private TrackingEntity tracking;

}
