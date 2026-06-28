package br.edu.ifpb.ads.foodjava.model;

public class Cliente extends Usuario {

    private String cpf;
    private String endereco;

    public Cliente() {
        super();
    }

    public Cliente(String nome, String email, String senha,
                   String telefone, String cpf, String endereco) {

        super(nome, email, senha, telefone);
        this.cpf = cpf;
        this.endereco = endereco;
    }

    @Override
    public boolean validar() {
        return super.validar()
                && textoPreenchido(cpf)
                && textoPreenchido(endereco);
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }
}