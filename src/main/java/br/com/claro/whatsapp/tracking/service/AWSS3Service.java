package br.com.claro.whatsapp.tracking.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		LOGGER.info("UPLOAD DO ARQUIVO: {} FEITO NO S3: {} BYTES", trackingCsvKey, putObject.getMetadata().getContentLength());
		return putObject;
	}

}
