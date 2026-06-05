package br.edu.ifpb.ads.foodjava.model;

public class Gerente extends Usuario {
    private String cargo;

    public Gerente() {
    }

    public Gerente(String id, String nome, String email, String senha, String telefone, String cargo) {
        super(id, nome, email, senha, telefone);
        this.cargo = cargo;
    }

    @Override
    public String getTipoUsuario() {
        return "GERENTE";
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
