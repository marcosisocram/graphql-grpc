package com.marcospaulo.service;

import com.google.protobuf.Empty;
import com.marcospaulo.app.grpc.cliCore.Cli;
import com.marcospaulo.app.grpc.cliCore.ClienteServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClienteService {

    private static Map<String, Impl.ClienteDTO> map;

    static {
        map = new HashMap<>();
    }

    private ClienteService() {
        //pass
    }

    public static class Impl extends ClienteServiceGrpc.ClienteServiceImplBase {

        @Override
        public void getAll(Cli.ClienteSearch request, StreamObserver<Cli.ClientePaginado> responseObserver) {
            final List<Cli.Cliente> enderecos = new ArrayList<>();

            for (ClienteDTO clienteDTO : map.values()) {
                final Cli.Cliente cliente = mapperClienteDtoToClienteGrpc(clienteDTO);
                enderecos.add(cliente);
            }

            final Cli.ClientePaginado clientePaginado = Cli.ClientePaginado.newBuilder().setPage(
                    request.getPage()).setPerPage(request.getPerPage()).setTotal(map.size()).addAllCliente(
                    enderecos).build();

            responseObserver.onNext(clientePaginado);
            responseObserver.onCompleted();
        }

        @Override
        public void get(Cli.IdentificadorCliente request, StreamObserver<Cli.Cliente> responseObserver) {
            final String id = request.getId();

            final ClienteDTO clienteDTO = map.get(id);

            responseObserver.onNext(mapperClienteDtoToClienteGrpc(clienteDTO));

            responseObserver.onCompleted();
        }


        @Override
        public void post(Cli.InputCliente request, StreamObserver<Cli.IdentificadorCliente> responseObserver) {
            final String id = UUID.randomUUID().toString();


            final ClienteDTO clienteDTO = new ClienteDTO(id,
                    request.getNome(), request.getDocumento(), request.getEndereco().getId(),
                    getDataHoje(), UUID.randomUUID().toString());

            map.put(id, clienteDTO);

            responseObserver.onNext(Cli.IdentificadorCliente.newBuilder().setId(id).build());

            responseObserver.onCompleted();
        }

        @Override
        public void put(Cli.Cliente request, StreamObserver<Cli.Cliente> responseObserver) {


            final String id = request.getId();

            ClienteDTO clienteDTO = map.get(id);

            if (clienteDTO == null) {
                responseObserver.onNext(null);
                responseObserver.onCompleted();
                return;
            }

            clienteDTO = new ClienteDTO(id,
                    request.getNome(), request.getDocumento(), request.getEndereco().getId(),
                    getDataHoje(), UUID.randomUUID().toString());


            map.put(id, clienteDTO);

            responseObserver.onNext(mapperClienteDtoToClienteGrpc(clienteDTO));
            responseObserver.onCompleted();


        }

        @Override
        public void delete(Cli.IdentificadorCliente request, StreamObserver<Empty> responseObserver) {

            map.remove(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        private String getDataHoje() {
            return Calendar.getInstance().get(Calendar.YEAR) + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                    + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }

        private Cli.Cliente mapperClienteDtoToClienteGrpc(ClienteDTO clienteDTO) {

            if (clienteDTO == null) {
                return null;
            }


            final Cli.Cliente negociacao = Cli.Cliente.newBuilder()
                    .setId(clienteDTO.getId())
                    .setNome(clienteDTO.getNome())
                    .setDocumento(clienteDTO.getDocumento())
                    .setEndereco(Cli.IdentificadorEndereco.newBuilder().setId(clienteDTO.getIdEndereco()).build())
                    .setDataAtualizacao(clienteDTO.getDataAtualizacao())
                    .setUsuarioAtualizacao(clienteDTO.getUsuarioAtualizacao())
                    .build();

            return negociacao;
        }

        private class ClienteDTO {

            private String id;
            private String nome;
            private String documento;
            private String idEndereco;

            private String dataAtualizacao;
            private String usuarioAtualizacao;

            public ClienteDTO(String id, String nome, String documento, String idEndereco, String dataAtualizacao,
                    String usuarioAtualizacao) {
                this.id = id;
                this.nome = nome;
                this.documento = documento;
                this.idEndereco = idEndereco;
                this.dataAtualizacao = dataAtualizacao;
                this.usuarioAtualizacao = usuarioAtualizacao;
            }

            public String getId() {
                return id;
            }

            public String getNome() {
                return nome;
            }

            public String getDocumento() {
                return documento;
            }

            public String getIdEndereco() {
                return idEndereco;
            }

            public String getDataAtualizacao() {
                return dataAtualizacao;
            }

            public String getUsuarioAtualizacao() {
                return usuarioAtualizacao;
            }
        }

    }
}
