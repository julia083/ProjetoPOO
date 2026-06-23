package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.interfaces.Autenticavel;
import br.edu.ifpb.ads.foodjava.interfaces.Validavel;
import br.edu.ifpb.ads.foodjava.util.GeradorID;
import br.edu.ifpb.ads.foodjava.util.SenhaUtil;

public abstract class Usuario implements Autenticavel, Validavel {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;

    protected Usuario() {
    }

    protected Usuario(String nome, String email, String senha, String telefone) {
        this.id = GeradorID.gerar();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public abstract String getTipoUsuario();

    @Override
    public boolean autenticar(String email, String senha) {
        return this.email.equals(email) && SenhaUtil.verificarSenha(senha, this.senha);
    }

    @Override
    public boolean validar() {
        return textoPreenchido(nome)
                && textoPreenchido(email)
                && textoPreenchido(senha);
    }

    protected boolean textoPreenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}

