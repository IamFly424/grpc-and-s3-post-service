package org.iamfly.awss3service.S3;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;


@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public void uploadFile(String bucketName, String fileName, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                                .key(fileName)
                                        .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    public void downloadFile(String bucketName, String fileName, String downloadPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(Paths.get(downloadPath)));
    }

    public void deleteFile(String bucketName, String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public URL generateUrl(String bucketName, String fileName) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        Duration duration = Duration.ofDays(1);
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(builder -> {
            builder.getObjectRequest(getObjectRequest)
                    .signatureDuration(duration);
        });
        return presignedGetObjectRequest.url();
    }



}
