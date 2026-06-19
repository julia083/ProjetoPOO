package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.exception.PrecoInvalidoException;
import br.edu.ifpb.ads.foodjava.interfaces.Validavel;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;

public class ItemCardapio implements Validavel {

    private String nome;
    private String descricao;
    private double preco;
    private Categoria categoria;
    private boolean disponivel = true;
    private String imagemPath;

    public ItemCardapio() {
    }

    public ItemCardapio(String nome, String descricao, double preco, Categoria categoria,
                        boolean disponivel, String imagemPath) {
        this.nome = nome;
        this.descricao = descricao;
        setPreco(preco);
        this.categoria = categoria;
        this.disponivel = disponivel;
        this.imagemPath = imagemPath;
    }

    @Override
    public boolean validar() {
        return nome != null && !nome.isBlank() && preco > 0 && categoria != null;
    }

    public void ativar() {
        this.disponivel = true;
    }

    public void desativar() {
        this.disponivel = false;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco <= 0) {
            throw new PrecoInvalidoException("O preco do item deve ser maior que zero.");
        }
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void setImagemPath(String imagemPath) {
        this.imagemPath = imagemPath;
    }

    public String getImagemPath() {
        return imagemPath;
    }
}