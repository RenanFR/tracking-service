package br.com.claro.whatsapp.tracking.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.claro.whatsapp.tracking.persistence.entity.GlobalExtrasEntity;

public class GlobalExtrasEntityDeserializer extends JsonDeserializer<GlobalExtrasEntity> {
	
	@Override
	public GlobalExtrasEntity deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {
		String jsonGlobalExtras = p.getText();
		GlobalExtrasEntity entity = new ObjectMapper().readValue(jsonGlobalExtras, GlobalExtrasEntity.class);
		return entity;
	}

}
