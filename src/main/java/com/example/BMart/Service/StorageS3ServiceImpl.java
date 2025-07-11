package com.example.BMart.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class StorageS3ServiceImpl implements StorageS3Service{

	    @Value("${aws.accessKey}")
	    private String awsAccessKey;

	    @Value("${aws.secretKey}")
	    private String awsSecretKey;

	    @Value("${aws.region}")
	    private String awsRegion;

	    @Value("${aws.bucketName}")
	    private String awsBucketName;

	    @Override
	    public String uploadFileToS3(String key, InputStream inputStream) {
	        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
	        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	                .withRegion(awsRegion)
	                .build();

	        try {
	            // Convert inputStream to byte array to get length
	            byte[] bytes = inputStream.readAllBytes();
	            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

	            ObjectMetadata metadata = new ObjectMetadata();
	            metadata.setContentLength(bytes.length); // ✅ Required
	            metadata.setContentType("audio/mpeg");

	            PutObjectRequest request = new PutObjectRequest(awsBucketName, key, byteArrayInputStream, metadata);
	            PutObjectResult result = s3Client.putObject(request);
	            System.out.println("Upload ETag: " + result.getETag());

	            return result.getETag();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "Upload failed: " + e.getMessage();
	        }
	    }

		}


	 

	

