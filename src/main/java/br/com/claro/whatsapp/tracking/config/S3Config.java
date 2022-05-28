package br.com.claro.whatsapp.tracking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	@Autowired
	private EndpointConfiguration endpointConfiguration;

	@Value("${aws.sqs.accessKey}")
	private String accessKey;

	@Value("${aws.sqs.secretKey}")
	private String secretKey;

	@Bean
	public AmazonS3 amazonS3() {

		return AmazonS3ClientBuilder.standard()
				.withPathStyleAccessEnabled(true)
				.withEndpointConfiguration(endpointConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}

}
