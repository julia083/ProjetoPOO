package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
public class ValidadorCPF {



    public static String limpar(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }
}
