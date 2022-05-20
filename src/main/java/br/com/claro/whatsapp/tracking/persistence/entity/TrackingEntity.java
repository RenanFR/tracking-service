package br.com.claro.whatsapp.tracking.persistence.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tracking")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEntity {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="chatbot_id")
    private String chatbotId;
    
    @Column(name="original_user_id")
    private String originalUserId;
    
    @Column(name="global_extras")
    private String globalExtras;
    
    @Column(name="user_phone")
    private String userPhone;
    
    @Column
    private String category;
    
    @Column(name="campaign_source")
    private String campaignSource;
    
    @Column(name="record_date")
    private LocalDateTime recordDate;

}
