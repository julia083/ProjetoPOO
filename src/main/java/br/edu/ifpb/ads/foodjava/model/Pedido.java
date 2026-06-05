package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.exception.CancelamentoNaoPermitidoException;
import br.edu.ifpb.ads.foodjava.exception.CarrinhoVazioException;
import br.edu.ifpb.ads.foodjava.exception.StatusInvalidoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido implements Validavel {
    private String id;
    private Cliente cliente;
    private LocalDateTime dataHora;
    private List<ItemPedido> itens = new ArrayList<>();
    private StatusPedido status = StatusPedido.AGUARDANDO_CONFIRMACAO;

    public Pedido() {
        this.dataHora = LocalDateTime.now();
    }

    public Pedido(String id, Cliente cliente) {
        this();
        this.id = id;
        this.cliente = cliente;
    }

    @Override
    public boolean validar() {
        return cliente != null && !itens.isEmpty();
    }

    public void adicionarItem(ItemCardapio itemCardapio, int quantidade) {
        itens.add(new ItemPedido(itemCardapio, quantidade));
    }

    public void removerItem(ItemPedido itemPedido) {
        itens.remove(itemPedido);
    }

    public double calcularTotal() {
        return itens.stream()
                .mapToDouble(ItemPedido::calcularSubtotal)
                .sum();
    }

    public double getValorTotal() {
        return calcularTotal();
    }

    public void confirmar() {
        if (itens.isEmpty()) {
            throw new CarrinhoVazioException("Nao e possivel confirmar um pedido sem itens.");
        }
    }

    public void avancarStatus() {
        switch (status) {
            case AGUARDANDO_CONFIRMACAO -> status = StatusPedido.CONFIRMADO;
            case CONFIRMADO -> status = StatusPedido.EM_PREPARO;
            case EM_PREPARO -> status = StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA -> status = StatusPedido.ENTREGUE;
            default -> throw new StatusInvalidoException("Nao e possivel avancar o status atual: " + status);
        }
    }

    public void cancelar() {
        if (status != StatusPedido.AGUARDANDO_CONFIRMACAO) {
            throw new CancelamentoNaoPermitidoException("Pedido so pode ser cancelado enquanto aguarda confirmacao.");
        }
        status = StatusPedido.CANCELADO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = new ArrayList<>(itens);
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}
