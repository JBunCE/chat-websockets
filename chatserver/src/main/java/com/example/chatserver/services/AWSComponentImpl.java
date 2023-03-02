package com.example.chatserver.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.chatserver.controllers.dtos.BaseResponse;
import com.example.chatserver.services.intefaces.IAWSComponent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class AWSComponentImpl implements IAWSComponent {

    private AmazonS3 s3Client;

    @Value(value = "${aws.endpoint-url}")
    private String endpointUrl;

    @Value(value = "${aws.bucket-name}")
    private String bucketName;

    @Value(value = "${aws.secret-key}")
    private String secretKey;

    @Value(value = "${aws.access-key}")
    private String accessKey;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl;
        try{
            File file = convertMultipartToFile(multipartFile);
            String finalPath = "files/" + generateFileName(multipartFile);
            fileUrl = "https://" + bucketName + "." + endpointUrl + "/" + finalPath;
            uploadFileTos3Bucket(finalPath, file);
            file.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileUrl;
    }

    @PostConstruct
    private void initializeAmazon() {
        log.info(accessKey);
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convFile)){
            fos.write(file.getBytes());
        }catch (Exception e){
            throw new RuntimeException();
        }
        return convFile;
    }

    private String generateFileName(MultipartFile multipartFile) {
        return Objects
                .requireNonNull(multipartFile.getOriginalFilename())
                .replace(" ", "_");
    }

    private void uploadFileTos3Bucket(String fileName, File file) {
        log.info(bucketName);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }



}
