package br.com.claro.whatsapp.tracking.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

@Service
public class DiscordTrackingBot {

	private GatewayDiscordClient client;

	@Autowired
	public DiscordTrackingBot(GatewayDiscordClient client) {
		this.client = client;
	}

	public void notifyNewTrackingReport() {
		client.on(ReadyEvent.class, event ->
        Mono.fromRunnable(() -> {
          final User self = event.getSelf();
          System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
        }));
//		client.rest().getChannelById(Snowflake.of("978762610868633680")).createMessage("Teste").subscribe();
	}

}
