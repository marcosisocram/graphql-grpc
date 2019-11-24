package com.marcospaulo.app;

import com.marcospaulo.service.EnderecoService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50052).addService(new EnderecoService.Impl()).build();

        server.start();
        System.out.println("Servidor iniciado na porta 50052");
        server.awaitTermination();
    }
}
