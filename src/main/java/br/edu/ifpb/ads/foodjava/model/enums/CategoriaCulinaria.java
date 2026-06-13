package br.edu.ifpb.ads.foodjava.model.enums;

public enum CategoriaCulinaria {

    NORDESTINA("Nordestina"),
    BRASILEIRA("Brasileira"),
    ITALIANA("Italiana"),
    JAPONESA("Japonesa"),
    PIZZARIA("Pizzaria"),
    HAMBURGUERIA("Hamburgueria"),
    DOCERIA("Doceria"),
    CAFETERIA("Cafeteria"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaCulinaria(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}