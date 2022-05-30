package br.com.claro.whatsapp.tracking.service.aws;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class AWSS3Service {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Service.class);

	private static final String BUCKET_NAME = "tracking-csv-bucket";

	private final AmazonS3 s3;

	@Autowired
	public AWSS3Service(AmazonS3 s3) {
		this.s3 = s3;
	}

	public PutObjectResult upload(String trackingCsvKey, File trackingFile) {
		PutObjectResult putObject = s3.putObject(BUCKET_NAME, trackingCsvKey, trackingFile);
		LOGGER.info("UPLOAD DO ARQUIVO: {} FEITO NO S3: {} BYTES", trackingCsvKey,
				putObject.getMetadata().getContentLength());
		return putObject;
	}

	public String getPresignedForUrlTrackingCsv(String trackingCsvKey) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
		String presignedUrl = s3.generatePresignedUrl(BUCKET_NAME, trackingCsvKey, calendar.getTime(), HttpMethod.GET).toString();
		return presignedUrl;

	}

}
