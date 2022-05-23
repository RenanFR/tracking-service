package br.com.claro.whatsapp.tracking.persistence.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.claro.whatsapp.tracking.mapper.GlobalExtrasEntityDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="tracking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEntity {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="chatbot_id")
    private String chatbotId;
    
    @Column(name="session_id")
    private String sessionId;
    
    @Column(name="original_user_id")
    private String originalUserId;
    
    @Column(name="global_extras_raw")
    private String globalExtrasRaw;
    
    @Column(name="user_phone")
    private String userPhone;
    
    @Column
    private String category;
    
    @Column
    private String type;
    
    @Column
    private String suffix;
    
    @Column(name="campaign_source")
    private String campaignSource;
    
    @Column(name="record_date")
    private LocalDateTime recordDate;
 
    @JsonDeserialize(using = GlobalExtrasEntityDeserializer.class)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "global_extras_id", referencedColumnName = "id")
    private GlobalExtrasEntity globalExtras;

}
