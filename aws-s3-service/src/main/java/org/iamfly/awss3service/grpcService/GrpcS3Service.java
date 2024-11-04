package org.iamfly.awss3service.grpcService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.iamfly.awss3service.S3.S3Service;
import org.springframework.stereotype.Component;
import posts3.Posts3;
import posts3.S3ServiceGrpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class GrpcS3Service extends S3ServiceGrpc.S3ServiceImplBase {

    private final S3Service s3Service;

    public GrpcS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public void uploadImage(Posts3.UploadImageRequest request, StreamObserver<Posts3.UploadImageResponse> responseObserver) {
        String bucketName = request.getBucketName();
        String keyName = request.getKeyName();
        byte[] file = request.getFileData().toByteArray();
        String filePath = request.getFilePath();
        try {
            s3Service.uploadFile(bucketName, keyName, toFile(file, filePath));
            responseObserver.onNext(Posts3.UploadImageResponse.newBuilder().setMessage("add to S3").build());
            responseObserver.onCompleted();
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL.withDescription("Error uploading file").withCause(e).asRuntimeException());

        }
    }

    @Override
    public void downloadImage(Posts3.DownloadImageRequest request, StreamObserver<Posts3.DownloadImageResponse> responseObserver) {
        String bucketName = request.getBucketName();
        String keyName = request.getKeyName();

        String url = s3Service.generateUrl(bucketName, keyName).toString();
        responseObserver.onNext(Posts3.DownloadImageResponse.newBuilder().setMessage(url).build());
        responseObserver.onCompleted();
    }

    private File toFile(byte[] file, String fileName) throws IOException {
        File filePath = new File(fileName);
        try(FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(file);
        }
        return filePath;
    }

}