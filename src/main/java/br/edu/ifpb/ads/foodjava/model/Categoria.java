package br.edu.ifpb.ads.foodjava.model;

public enum Categoria {
    ENTRADA(1),
    PRATO_PRINCIPAL(2),
    SOBREMESA(3),
    BEBIDAS(4);

    private int code;
    Categoria(int code){
        this.code = code;
    }
}