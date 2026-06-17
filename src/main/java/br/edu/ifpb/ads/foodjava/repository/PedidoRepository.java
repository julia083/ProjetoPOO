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

    /**
     * Busca todos os pedidos de um cliente pelo email.
     * @param emailCliente Email do cliente
     * @return Lista de pedidos do cliente (não modificável)
     */
    public static List<Pedido> buscarPorCliente(String emailCliente) {
        if (emailCliente == null || emailCliente.isBlank()) {
            return Collections.emptyList();
        }

        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getCliente() != null && emailCliente.equalsIgnoreCase(p.getCliente().getEmail())) {
                resultado.add(p);
            }
        }
        return Collections.unmodifiableList(resultado);
    }
}