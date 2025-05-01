package com.example.document_management.repo;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Repository
public class S3Repository {

    @Autowired
    private AmazonS3 s3Client;

    @Value("#{systemEnvironment['AWS_BUCKET_NAME']}")
    private String bucketName;


    public void uploadFile(String key, InputStream inputStream, ObjectMetadata metadata) {
        System.out.println("✅ Bean s3Client creado con region: " + bucketName);

        s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));
    }

    public S3Object downloadFile(String key) {
        return s3Client.getObject(bucketName, key);
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    public List<String> listFiles() {
        return s3Client.listObjects(bucketName)
                .getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
}
