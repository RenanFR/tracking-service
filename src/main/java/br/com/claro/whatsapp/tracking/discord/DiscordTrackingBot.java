package br.com.claro.whatsapp.tracking.discord;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.MessageData;

@Service
public class DiscordTrackingBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiscordTrackingBot.class);

	private GatewayDiscordClient client;

	@Value("${discord.channelId}")
	private String channelId;

	@Autowired
	public DiscordTrackingBot(GatewayDiscordClient client) {
		this.client = client;
	}

	public MessageData notifyNewTrackingReport(String fileS3Url) throws IOException {

		MessageCreateSpec messageWithFile = MessageCreateSpec.builder()
				.content("O relatório diário de Tracking já está disponível em: " + fileS3Url).build();

		MessageData blockMessageData = client.rest().getChannelById(Snowflake.of(channelId))
				.createMessage(messageWithFile.asRequest().getJsonPayload()).block();
		return blockMessageData;
	}

}
