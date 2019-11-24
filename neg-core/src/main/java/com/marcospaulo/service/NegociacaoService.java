package com.marcospaulo.app.service;

import com.google.protobuf.Empty;
import com.marcospaulo.app.grpc.negCore.Neg;
import com.marcospaulo.app.grpc.negCore.NegociacaoServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class NegociacaoService {

    private static Map<String, Impl.NegociacaoDTO> map;

    static {
        map = new HashMap<>();
    }

    private NegociacaoService() {
        //pass
    }


    public static class Impl extends NegociacaoServiceGrpc.NegociacaoServiceImplBase {

        @Override
        public void getAll(Neg.NegociacaoSearch request, StreamObserver<Neg.NegociacaoPaginado> responseObserver) {


            final List<Neg.Negociacao> neg = new ArrayList<>();

            for (NegociacaoDTO negociacaoDTO : map.values()) {


                final Neg.Negociacao negociacao = mapperNegociacaoDtoToNegociacaoGrpc(negociacaoDTO);


                neg.add(negociacao);
            }


            final Neg.NegociacaoPaginado negociacaoPaginado = Neg.NegociacaoPaginado.newBuilder().setPage(
                    request.getPage()).setPerPage(request.getPerPage()).setTotal(map.size()).addAllNegociacao(
                    neg).build();

            responseObserver.onNext(negociacaoPaginado);
            responseObserver.onCompleted();
        }

        private Neg.Negociacao mapperNegociacaoDtoToNegociacaoGrpc(NegociacaoDTO negociacaoDTO) {

            if (negociacaoDTO == null) {
                return null;
            }


            final Neg.IdentificadorCliente cliente = Neg.IdentificadorCliente
                    .newBuilder()
                    .setId(negociacaoDTO.getIdCliente()).build();

            final Neg.IdentificadorEndereco endereco = Neg.IdentificadorEndereco.newBuilder().setId(
                    negociacaoDTO.getIdEndereco()).build();

            final Neg.Negociacao negociacao = Neg.Negociacao.newBuilder().setId(negociacaoDTO.getId())
                    .setCliente(
                            cliente).setDataAtualizacao(
                            negociacaoDTO.getDataAtualizacao()).setUsuarioAtualizacao(
                            negociacaoDTO.getUsuarioAtualizacao()).setEndereco(
                            endereco)
                    .setProduto(Neg.Produto.forNumber(negociacaoDTO.getProduto())).build();

            return negociacao;
        }

        @Override
        public void get(Neg.IdentificadorNegociacao request,
                io.grpc.stub.StreamObserver<Neg.Negociacao> responseObserver) {


            final String id = request.getId();

            final NegociacaoDTO negociacaoDTO = map.get(id);

            responseObserver.onNext(mapperNegociacaoDtoToNegociacaoGrpc(negociacaoDTO));

            responseObserver.onCompleted();
        }

        @Override
        public void post(Neg.InputNegociacao request,
                io.grpc.stub.StreamObserver<Neg.IdentificadorNegociacao> responseObserver) {

            final String id = UUID.randomUUID().toString();


            final NegociacaoDTO negociacaoDTO = new NegociacaoDTO(id,
                    request.getCliente().getId(), request.getEndereco().getId(), request.getProdutoValue(),
                    Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(
                            Calendar.MONTH) + 1) + "-" +
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH), UUID.randomUUID().toString());

            map.put(id, negociacaoDTO);

            responseObserver.onNext(Neg.IdentificadorNegociacao.newBuilder().setId(id).build());

            responseObserver.onCompleted();

        }

        @Override
        public void put(Neg.Negociacao request,
                io.grpc.stub.StreamObserver<Neg.Negociacao> responseObserver) {

            final String id = request.getId();
            NegociacaoDTO negociacaoDTO = map.get(id);

            if (negociacaoDTO == null) {
                responseObserver.onNext(null);
                responseObserver.onCompleted();
                return;
            }

            negociacaoDTO = new NegociacaoDTO(id,
                    request.getCliente().getId(), request.getEndereco().getId(), request.getProdutoValue(),
                    Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(
                            Calendar.MONTH) + 1 + "-" +
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH), UUID.randomUUID().toString());


            map.put(id, negociacaoDTO);

            responseObserver.onNext(mapperNegociacaoDtoToNegociacaoGrpc(negociacaoDTO));
            responseObserver.onCompleted();

        }


        @Override
        public void delete(Neg.IdentificadorNegociacao request,
                io.grpc.stub.StreamObserver<Empty> responseObserver) {

            map.remove(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }


        private class NegociacaoDTO {

            private String id;
            private String idCliente;
            private String idEndereco;
            private Integer produto;
            private String dataAtualizacao;
            private String usuarioAtualizacao;

            NegociacaoDTO(String id, String idCliente, String idEndereco, Integer produto, String dataAtualizacao,
                    String usuarioAtualizacao) {
                this.id = id;
                this.idCliente = idCliente;
                this.idEndereco = idEndereco;
                this.produto = produto;
                this.dataAtualizacao = dataAtualizacao;
                this.usuarioAtualizacao = usuarioAtualizacao;
            }

            public String getId() {
                return id;
            }

            public String getIdCliente() {
                return idCliente;
            }

            public String getIdEndereco() {
                return idEndereco;
            }

            public Integer getProduto() {
                return produto;
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
