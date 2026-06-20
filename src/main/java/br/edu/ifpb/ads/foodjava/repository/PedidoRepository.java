package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.exception.CarrinhoVazioException;
import br.edu.ifpb.ads.foodjava.interfaces.Repositorio;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.model.enums.StatusPedido;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoRepository implements Repositorio<Pedido> {

    private static final String CAMINHO = "data/pedidos.json";
    private static final Type TIPO_LISTA = new TypeToken<List<Pedido>>() {}.getType();

    @Override
    public List<Pedido> listarTodos() {
        return JsonUtil.ler(CAMINHO, TIPO_LISTA, new ArrayList<>());
    }

    @Override
    public void salvarTodos(List<Pedido> pedidos) {
        JsonUtil.escrever(CAMINHO, pedidos);
    }

    public Optional<Pedido> buscarPorId(String id) {
        return listarTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Pedido> listarPorCliente(String clienteId) {
        return listarTodos().stream()
                .filter(p -> p.getClienteId() != null && p.getClienteId().equals(clienteId))
                .toList();
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return listarTodos().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }

    public void adicionar(Pedido pedido) {
        if (!pedido.validar()) {
            throw new CarrinhoVazioException("Nao e possivel salvar um pedido sem itens ou sem cliente.");
        }

        List<Pedido> pedidos = listarTodos();
        pedidos.add(pedido);
        salvarTodos(pedidos);
    }

    public void atualizar(Pedido pedidoAtualizado) {
        List<Pedido> pedidos = listarTodos();

        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId().equals(pedidoAtualizado.getId())) {
                pedidos.set(i, pedidoAtualizado);
                break;
            }
        }

        salvarTodos(pedidos);
    }

    public double calcularFaturamentoDoDia() {
        LocalDate hoje = LocalDate.now();
        return listarTodos().stream()
                .filter(p -> p.getDataHora() != null && p.getDataHora().toLocalDate().equals(hoje))
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .mapToDouble(Pedido::calcularTotal)
                .sum();
    }

    public long contarPedidosDoDia() {
        LocalDate hoje = LocalDate.now();
        return listarTodos().stream()
                .filter(p -> p.getDataHora() != null && p.getDataHora().toLocalDate().equals(hoje))
                .count();
    }
}