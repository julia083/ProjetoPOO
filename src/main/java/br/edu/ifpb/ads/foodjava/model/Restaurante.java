package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.interfaces.Validavel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurante implements Validavel {
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String categoriaCulinaria;
    private String email;
    private String logotipoPath;
    private Gerente gerente;
    private List<ItemCardapio> cardapio = new ArrayList<>();

    public Restaurante() {
    }

    public Restaurante(String nomeFantasia, String cnpj, String endereco, String telefone,
                       String categoriaCulinaria, String email, String logotipoPath, Gerente gerente) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.telefone = telefone;
        this.categoriaCulinaria = categoriaCulinaria;
        this.email = email;
        this.logotipoPath = logotipoPath;
        this.gerente = gerente;
    }

    @Override
    public boolean validar() {
        return textoPreenchido(nomeFantasia)
                && textoPreenchido(cnpj)
                && textoPreenchido(endereco)
                && textoPreenchido(email)
                && gerente != null;
    }

    public void adicionarItemCardapio(ItemCardapio item) {
        cardapio.add(item);
    }

    public void removerItemCardapio(ItemCardapio item) {
        cardapio.remove(item);
    }

    private boolean textoPreenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCategoriaCulinaria() {
        return categoriaCulinaria;
    }

    public void setCategoriaCulinaria(String categoriaCulinaria) {
        this.categoriaCulinaria = categoriaCulinaria;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogotipoPath() {
        return logotipoPath;
    }

    public void setLogotipoPath(String logotipoPath) {
        this.logotipoPath = logotipoPath;
    }

    public Gerente getGerente() {
        return gerente;
    }

    public void setGerente(Gerente gerente) {
        this.gerente = gerente;
    }

    public List<ItemCardapio> getCardapio() {
        return Collections.unmodifiableList(cardapio);
    }

    public void setCardapio(List<ItemCardapio> cardapio) {
        this.cardapio = new ArrayList<>(cardapio);
    }
}
