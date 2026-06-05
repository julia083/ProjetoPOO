package br.edu.ifpb.ads.foodjava.model;

public class Cliente extends Usuario {
    private String cpf;
    private String endereco;

    public Cliente() {
    }

    public Cliente(String id, String nome, String email, String senha, String telefone, String cpf, String endereco) {
        super(id, nome, email, senha, telefone);
        this.cpf = cpf;
        this.endereco = endereco;
    }

    @Override
    public String getTipoUsuario() {
        return "CLIENTE";
    }

    @Override
    public boolean validar() {
        return super.validar() && textoPreenchido(cpf) && textoPreenchido(endereco);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
