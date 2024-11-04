package org.iamfly.awss3service.grpcService;

import io.grpc.Server;

import io.grpc.ServerBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
public class GrpcServer {

    private Server server;
    private final GrpcS3Service grpcS3Service;

    public GrpcServer(GrpcS3Service grpcS3Service) {
        this.grpcS3Service = grpcS3Service;
    }


    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(9060)
                .addService(grpcS3Service)
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(GrpcServer.this::stop));
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

}
