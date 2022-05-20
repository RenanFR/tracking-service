package br.com.claro.whatsapp.tracking.mapper;

import org.mapstruct.Mapper;

import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;

@Mapper(componentModel = "spring")
public interface TrackingMapper {
	
	Tracking entityToRecord(TrackingEntity tracking);
	
	TrackingEntity recordToEntity(Tracking tracking);

}
