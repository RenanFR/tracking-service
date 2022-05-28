package br.com.claro.whatsapp.tracking.discord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;

@Configuration
public class DiscordTrackingBotConfig {

	@Value("${discord.token}")
	private String discordToken;

	@Bean
	public GatewayDiscordClient gatewayDiscordClient() {
		return DiscordClientBuilder.create(discordToken).build().login().block();
	}

}
