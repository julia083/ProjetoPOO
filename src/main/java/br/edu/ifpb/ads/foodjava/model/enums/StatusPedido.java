package br.edu.ifpb.ads.foodjava.model.enums;

public enum StatusPedido {
    AGUARDANDO_CONFIRMACAO(1),
    CONFIRMADO(2),
    EM_PREPARO(3),
    SAIU_PARA_ENTREGA(4),
    ENTREGUE(5),
    CANCELADO(6);

    StatusPedido(int code){}

}
