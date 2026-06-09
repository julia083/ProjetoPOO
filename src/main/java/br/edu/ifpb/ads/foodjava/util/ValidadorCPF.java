package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
public class ValidadorCPF {



    public static String limpar(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }


    public static void validar(String cpf) throws DocumentoInvalidoException {
        String c = limpar(cpf);

        if (c.length() != 11)
            throw new DocumentoInvalidoException("CPF deve conter 11 dígitos.");

        // rejeita CPFs com todos os dígitos iguais (ex: 111.111.111-11)
        if (c.chars().distinct().count() == 1)
            throw new DocumentoInvalidoException("CPF inválido.");

        // valida primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++)
            soma += Character.getNumericValue(c.charAt(i)) * (10 - i);
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        if (primeiroDigito != Character.getNumericValue(c.charAt(9)))
            throw new DocumentoInvalidoException("CPF inválido.");

        // valida segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++)
            soma += Character.getNumericValue(c.charAt(i)) * (11 - i);
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        if (segundoDigito != Character.getNumericValue(c.charAt(10)))
            throw new DocumentoInvalidoException("CPF inválido.");
    }
}

