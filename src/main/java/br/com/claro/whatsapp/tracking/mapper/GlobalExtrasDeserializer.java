package br.com.claro.whatsapp.tracking.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.claro.whatsapp.tracking.model.GlobalExtras;

public class GlobalExtrasDeserializer extends JsonDeserializer<GlobalExtras> {
	
	@Override
	public GlobalExtras deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {
		String jsonGlobalExtras = p.getText();
		GlobalExtras extras = new ObjectMapper().readValue(jsonGlobalExtras, GlobalExtras.class);
		return extras;
	}

}
