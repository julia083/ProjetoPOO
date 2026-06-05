package br.edu.ifpb.ads.foodjava.model;

import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;
import br.edu.ifpb.ads.foodjava.interfaces.Autenticavel;
import br.edu.ifpb.ads.foodjava.interfaces.Validavel;

public abstract class Usuario implements Autenticavel, Validavel {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;

    protected Usuario() {
    }

    protected Usuario(String id, String nome, String email, String senha, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        setSenha(senha);
        this.telefone = telefone;
    }

    public abstract String getTipoUsuario();

    @Override
    public boolean autenticar(String email, String senha) {
        return this.email != null
                && this.senha != null
                && this.email.equalsIgnoreCase(email)
                && this.senha.equals(senha);
    }

    @Override
    public boolean validar() {
        return textoPreenchido(nome)
                && textoPreenchido(email)
                && senhaValida(senha);
    }

    protected boolean textoPreenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    protected boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 8 && senha.chars().anyMatch(Character::isDigit);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        if (!senhaValida(senha)) {
            throw new SenhaInvalidaException("A senha deve ter pelo menos 8 caracteres e um digito numerico.");
        }
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
