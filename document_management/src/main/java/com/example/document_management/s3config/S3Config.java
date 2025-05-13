package com.example.document_management.s3config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
@Configuration
public class S3Config {

    @Value("#{systemEnvironment['AWS_ACCESS_KEY_ID']}")
    private String accessKey;

    @Value("#{systemEnvironment['AWS_SECRET_ACCESS_KEY']}")
    private String secretKey;

    @Value("#{systemEnvironment['AWS_REGION']}")
    private String region;

    @Bean
    public AmazonS3 s3Client() {

        if (accessKey == null || secretKey == null || region == null) {
            throw new IllegalArgumentException("Faltan variables AWS");
        }

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}