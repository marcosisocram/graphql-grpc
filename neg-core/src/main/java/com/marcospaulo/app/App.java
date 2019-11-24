package com.marcospaulo.app;

import com.marcospaulo.app.service.NegociacaoService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051).addService(new NegociacaoService.Impl() ).build();

        server.start();

        System.out.println("Servidor iniciado na porta 50051");

        server.awaitTermination();
    }
}
