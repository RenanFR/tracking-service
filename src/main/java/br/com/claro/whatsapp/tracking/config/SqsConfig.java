package br.com.claro.whatsapp.tracking.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.support.NotificationMessageArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SqsConfig {
	
	@Value("${aws.sqs.serviceEndpoint}")
	private String serviceEndpoint;
	
	@Value("${aws.sqs.signingRegion}")
	private String signingRegion;
	
	@Value("${aws.sqs.accessKey}")
	private String accessKey;
	
	@Value("${aws.sqs.secretKey}")
	private String secretKey;

	@Bean
	public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
		return new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync(final AwsClientBuilder.EndpointConfiguration endpointConfiguration) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonSQSAsyncClientBuilder.standard().withEndpointConfiguration(endpointConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}

	@Bean
	public QueueMessageHandlerFactory queueMessageHandlerFactory(MessageConverter messageConverter) {
		var factory = new QueueMessageHandlerFactory();
		factory.setArgumentResolvers(List.of(new NotificationMessageArgumentResolver(messageConverter)));
		return factory;
	}

	@Bean
	protected MessageConverter messageConverter(@Autowired ObjectMapper customObjectMapper) {
		var converter = new MappingJackson2MessageConverter();
		converter.setSerializedPayloadClass(String.class);
		converter.setStrictContentTypeMatch(false);
		converter.setObjectMapper(customObjectMapper);
		return converter;
	}

}
