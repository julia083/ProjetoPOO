package br.edu.ifpb.ads.foodjava.model;

public class Gerente extends Usuario {

    private String cpf;

    public Gerente() {
        super();
    }

    public Gerente(String id, String nome, String email, String senha,
                   String telefone, String cpf) {

        super(id, nome, email, senha, telefone);
        this.cpf = cpf;
    }

    @Override
    public String getTipoUsuario() {
        return "GERENTE";
    }

    @Override
    public boolean validar() {
        return super.validar()
                && textoPreenchido(cpf);
    }

    public String getCpf() {
        return cpf;
    }

}