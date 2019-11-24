package com.marcospaulo.service;

import com.google.protobuf.Empty;
import com.marcospaulo.app.grpc.endrCore.EnderecoServiceGrpc;
import com.marcospaulo.app.grpc.endrCore.Endr;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EnderecoService {


    private static Map<String, Impl.EnderecoDTO> map;

    static {
        map = new HashMap<>();
    }

    private EnderecoService() {
        //pass
    }

    public static class Impl extends EnderecoServiceGrpc.EnderecoServiceImplBase {
        @Override
        public void getAll(Endr.EnderecoSearch request, StreamObserver<Endr.EnderecoPaginado> responseObserver) {

            final List<Endr.Endereco> enderecos = new ArrayList<>();

            for (EnderecoDTO enderecoDTO : map.values()) {

                final Endr.Endereco negociacao = mapperEnderecoDtoToEnderecoGrpc(enderecoDTO);
                enderecos.add(negociacao);
            }

            final Endr.EnderecoPaginado enderecoPaginado = Endr.EnderecoPaginado.newBuilder().setPage(
                    request.getPage()).setPerPage(request.getPerPage()).setTotal(map.size()).addAllEndereco(
                    enderecos).build();

            responseObserver.onNext(enderecoPaginado);
            responseObserver.onCompleted();
        }


        @Override
        public void get(Endr.IdentificadorEndereco request, StreamObserver<Endr.Endereco> responseObserver) {
            final String id = request.getId();

            final EnderecoDTO enderecoDTO = map.get(id);

            responseObserver.onNext(mapperEnderecoDtoToEnderecoGrpc(enderecoDTO));

            responseObserver.onCompleted();
        }

        @Override
        public void post(Endr.InputEndereco request, StreamObserver<Endr.IdentificadorEndereco> responseObserver) {
            final String id = UUID.randomUUID().toString();


            final EnderecoDTO negociacaoDTO = new EnderecoDTO(id,
                    request.getCep(), request.getRua(), request.getComplemento(), request.getNumero(),
                    getDataHoje(), UUID.randomUUID().toString());

            map.put(id, negociacaoDTO);

            responseObserver.onNext(Endr.IdentificadorEndereco.newBuilder().setId(id).build());

            responseObserver.onCompleted();
        }


        @Override
        public void put(Endr.Endereco request, StreamObserver<Endr.Endereco> responseObserver) {

            final String id = request.getId();

            EnderecoDTO enderecoDTO = map.get(id);

            if (enderecoDTO == null) {
                responseObserver.onNext(null);
                responseObserver.onCompleted();
                return;
            }

            enderecoDTO = new EnderecoDTO(id,
                    request.getCep(), request.getRua(), request.getComplemento(), request.getNumero(),
                    getDataHoje(), UUID.randomUUID().toString());


            map.put(id, enderecoDTO);

            responseObserver.onNext(mapperEnderecoDtoToEnderecoGrpc(enderecoDTO));
            responseObserver.onCompleted();

        }

        @Override
        public void delete(Endr.IdentificadorEndereco request, StreamObserver<Empty> responseObserver) {


            map.remove(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }


        private String getDataHoje() {
            return Calendar.getInstance().get(Calendar.YEAR) + "-"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-"
                    + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }

        private Endr.Endereco mapperEnderecoDtoToEnderecoGrpc(EnderecoDTO enderecoDTO) {

            if (enderecoDTO == null) {
                return null;
            }


            final Endr.Endereco negociacao = Endr.Endereco.newBuilder()
                        .setId(enderecoDTO.getId())
                        .setCep(enderecoDTO.cep)
                        .setRua(enderecoDTO.getRua())
                        .setNumero(enderecoDTO.getNumero())
                        .setComplemento(enderecoDTO.getComplemento())
                        .setDataAtualizacao(enderecoDTO.getDataAtualizacao())
                        .setUsuarioAtualizacao(enderecoDTO.getUsuarioAtualizacao())
                    .build();

            return negociacao;
        }

        private class EnderecoDTO {

            private String id;
            private String cep;
            private String rua;
            private String complemento;
            private String numero;

            private String dataAtualizacao;
            private String usuarioAtualizacao;

            public EnderecoDTO(String id, String cep, String rua, String complemento, String numero,
                    String dataAtualizacao, String usuarioAtualizacao) {
                this.id = id;
                this.cep = cep;
                this.rua = rua;
                this.complemento = complemento;
                this.numero = numero;
                this.dataAtualizacao = dataAtualizacao;
                this.usuarioAtualizacao = usuarioAtualizacao;
            }

            public String getCep() {
                return cep;
            }

            public String getRua() {
                return rua;
            }

            public String getComplemento() {
                return complemento;
            }

            public String getNumero() {
                return numero;
            }

            public String getId() {
                return id;
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
