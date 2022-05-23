package br.com.claro.whatsapp.tracking.mapper;

import org.apache.commons.text.StringEscapeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.claro.whatsapp.tracking.model.GlobalExtras;
import br.com.claro.whatsapp.tracking.model.Tracking;
import br.com.claro.whatsapp.tracking.persistence.entity.GlobalExtrasEntity;
import br.com.claro.whatsapp.tracking.persistence.entity.TrackingEntity;

@Mapper(componentModel = "spring")
public abstract class TrackingMapper {

	private ObjectMapper customObjectMapper;

	@Mapping(source = "globalExtrasRaw", target = "globalExtras", qualifiedByName = "fromJsonToGlobalExtras")
	public abstract Tracking entityToRecord(TrackingEntity tracking);

	@Mapping(source = "globalExtrasRaw", target = "globalExtras", qualifiedByName = "fromJsonToGlobalExtrasEntity")
	public abstract TrackingEntity recordToEntity(Tracking tracking);

	@Named("fromJsonToGlobalExtras")
	public GlobalExtras fromJsonToGlobalExtras(String jsonExtras) throws JsonProcessingException {
		if (jsonExtras == null) {
			return null;
		}
		String unescapedJson = StringEscapeUtils.unescapeJson(jsonExtras).replaceAll("^\"|\"$", "");
		GlobalExtras extras = customObjectMapper.readValue(unescapedJson, GlobalExtras.class);
		return extras;
	}

	@Named("fromJsonToGlobalExtrasEntity")
	public GlobalExtrasEntity fromJsonToGlobalExtrasEntity(String jsonExtras) throws JsonProcessingException {
		if (jsonExtras == null) {
			return null;
		}
		String unescapedJson = StringEscapeUtils.unescapeJson(jsonExtras).replaceAll("^\"|\"$", "");
		GlobalExtrasEntity extras = customObjectMapper.readValue(unescapedJson, GlobalExtrasEntity.class);
		return extras;
	}

	@Autowired
	public void setCustomObjectMapper(ObjectMapper customObjectMapper) {
		this.customObjectMapper = customObjectMapper;
	}

}
