package com.marcospaulo.app;

import com.marcospaulo.service.ClienteService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50053).addService(new ClienteService.Impl()).build();

        server.start();
        System.out.println("Servidor iniciado na porta 50053");

        server.awaitTermination();
    }
}
