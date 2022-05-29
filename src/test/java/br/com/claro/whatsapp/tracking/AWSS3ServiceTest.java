package br.com.claro.whatsapp.tracking;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.claro.whatsapp.tracking.config.ObjectMapperConfig;
import br.com.claro.whatsapp.tracking.config.S3Config;
import br.com.claro.whatsapp.tracking.config.SqsConfig;
import br.com.claro.whatsapp.tracking.service.AWSS3Service;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { ObjectMapperConfig.class, S3Config.class, SqsConfig.class, AWSS3Service.class })
public class AWSS3ServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceTest.class);

	@Autowired
	private AWSS3Service s3Service;

	@Test
	public void shouldUploadFileToS3AndThenGetPresignedUrl() throws IOException {

		String trackingCsvKey = "sample-tracking-report.csv";
		File sampleTrackingReport = new ClassPathResource(trackingCsvKey).getFile();

		String fileContent = FileUtils.readFileToString(sampleTrackingReport, StandardCharsets.UTF_8.name());

		LOGGER.info("CONTEUDO DO CSV: {}", fileContent);

		s3Service.upload(trackingCsvKey, sampleTrackingReport);

		String presignedForUrlTrackingCsv = s3Service.getPresignedForUrlTrackingCsv(trackingCsvKey);
		LOGGER.info("URL PRE-ASSINADA: {}", presignedForUrlTrackingCsv);

		assertNotNull(presignedForUrlTrackingCsv);
	}

}
