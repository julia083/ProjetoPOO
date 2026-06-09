package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;

public class ValidadorCNPJ {

    public static String limpar(String cnpj) {
        return cnpj.replaceAll("[^0-9]", "");
    }

    public static void validar(String cnpj) throws DocumentoInvalidoException {
        String c = limpar(cnpj);

        if (c.length() != 14)
            throw new DocumentoInvalidoException("CNPJ deve conter 14 dígitos.");

        // rejeita CNPJs com todos os dígitos iguais
        if (c.chars().distinct().count() == 1)
            throw new DocumentoInvalidoException("CNPJ inválido.");

        // valida primeiro dígito verificador
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++)
            soma += Character.getNumericValue(c.charAt(i)) * pesos1[i];
        int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        if (primeiroDigito != Character.getNumericValue(c.charAt(12)))
            throw new DocumentoInvalidoException("CNPJ inválido.");

        // valida segundo dígito verificador
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++)
            soma += Character.getNumericValue(c.charAt(i)) * pesos2[i];
        int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        if (segundoDigito != Character.getNumericValue(c.charAt(13)))
            throw new DocumentoInvalidoException("CNPJ inválido.");
    }
}

