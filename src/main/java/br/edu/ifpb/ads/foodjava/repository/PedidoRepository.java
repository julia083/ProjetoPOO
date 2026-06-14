package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.model.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PedidoRepository {

    private static final List<Pedido> pedidos = new ArrayList<>();

    private PedidoRepository() {
    }

    public static void salvar(Pedido pedido) {
        if (pedido == null || !pedido.validar()) {
            throw new IllegalArgumentException("Pedido invalido.");
        }

        pedidos.add(pedido);
    }

    public static List<Pedido> listarTodos() {
        return Collections.unmodifiableList(pedidos);
    }
}
