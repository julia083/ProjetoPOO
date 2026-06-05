package br.edu.ifpb.ads.foodjava.model;

public class ItemPedido {
    private ItemCardapio itemCardapio;
    private int quantidade;

    public ItemPedido() {
    }

    public ItemPedido(ItemCardapio itemCardapio, int quantidade) {
        this.itemCardapio = itemCardapio;
        setQuantidade(quantidade);
    }

    public double calcularSubtotal() {
        if (itemCardapio == null) {
            return 0;
        }
        return itemCardapio.getPreco() * quantidade;
    }

    public ItemCardapio getItemCardapio() {
        return itemCardapio;
    }

    public void setItemCardapio(ItemCardapio itemCardapio) {
        this.itemCardapio = itemCardapio;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;
    }
}
