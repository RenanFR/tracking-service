package br.com.claro.whatsapp.tracking;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.claro.whatsapp.tracking.discord.DiscordTrackingBot;
import br.com.claro.whatsapp.tracking.discord.DiscordTrackingBotConfig;
import discord4j.discordjson.json.MessageData;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { DiscordTrackingBotConfig.class, DiscordTrackingBot.class })
public class DiscordTrackingBotTest {

	@Autowired
	private DiscordTrackingBot bot;

	@Test
	public void shouldSendMessageToDiscordChannel() throws Exception {

		String fileS3Url = "http://localhost:4566/tracking-csv-bucket/sample-tracking-report.csv?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20220529T174301Z&X-Amz-SignedHeaders=host&X-Amz-Expires=86399&X-Amz-Credential=foo%2F20220529%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=28f5b60a2acc1dbf4686ffe62c109ab15e5ad347e4bac1d7941d3ec701d99b08";
		MessageData messageData = bot.notifyNewTrackingReport(fileS3Url);
		assertEquals(messageData.author().username(), "Tracking bot");
		assertTrue(messageData.author().bot().get());
		assertTrue(messageData.content().contains("O relatório diário de Tracking já está disponível em"));
		assertTrue(messageData.content().contains(fileS3Url));
	}

}
