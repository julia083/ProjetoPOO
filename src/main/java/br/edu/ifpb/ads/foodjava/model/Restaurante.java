package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.interfaces.Validavel;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurante implements Validavel {
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private String telefone;
    private CategoriaCulinaria categoriaCulinaria;
    private String logotipoPath;
    private Gerente gerente;
    private List<ItemCardapio> cardapio = new ArrayList<>();

    public Restaurante() {
    }

    public Restaurante(String nomeFantasia, String cnpj, String endereco, String telefone,
                       CategoriaCulinaria categoriaCulinaria, String logotipoPath, Gerente gerente) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.categoriaCulinaria = categoriaCulinaria;
        this.logotipoPath = logotipoPath;
        this.gerente = gerente;
    }

    @Override
    public boolean validar() {
        return textoPreenchido(nomeFantasia)
                && textoPreenchido(cnpj)
                && textoPreenchido(endereco)
                && textoPreenchido(telefone)
                && categoriaCulinaria != null
                && gerente != null;
    }

    private boolean textoPreenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public Gerente getGerente() {
        return gerente;
    }

    public List<ItemCardapio> getCardapio() {
        return Collections.unmodifiableList(cardapio);
    }

    public String getLogotipoPath(){
        return logotipoPath;
    }
}
