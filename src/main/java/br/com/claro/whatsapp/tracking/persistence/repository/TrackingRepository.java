package br.com.claro.whatsapp.tracking.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;

public interface TrackingRepository extends JpaRepository<TrackingEntity, Long> {
	
	List<TrackingEntity> findByRecordDateBetween(LocalDateTime from, LocalDateTime to);

}
