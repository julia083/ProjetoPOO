package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;

/**
 * Classe utilitária para validação de CPF e CNPJ.
 * Todos os métodos lançam {@link DocumentoInvalidoException} em caso de invalidade.
 */
public final class DocumentoUtil {

    private DocumentoUtil() {
        // Construtor privado para evitar instanciação
    }

    /**
     * Remove caracteres não numéricos de um documento.
     * @param doc CPF ou CNPJ com formatação
     * @return apenas números
     */
    public static String limpar(String doc) {
        return doc.replaceAll("[^0-9]", "");
    }

    /**
     * Valida um CPF, lançando exceção se inválido.
     * @param cpf CPF com ou sem formatação
     * @throws DocumentoInvalidoException se o CPF for inválido
     */
    public static void validarCpf(String cpf) throws DocumentoInvalidoException {
        String c = limpar(cpf);

        if (c.length() != 11) {
            throw new DocumentoInvalidoException("CPF deve conter 11 dígitos.");
        }

        // CPF com todos os dígitos iguais é inválido
        if (c.chars().distinct().count() == 1) {
            throw new DocumentoInvalidoException("CPF inválido.");
        }

        // Valida primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(c.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        if (primeiroDigito != Character.getNumericValue(c.charAt(9))) {
            throw new DocumentoInvalidoException("CPF inválido.");
        }

        // Valida segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(c.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        if (segundoDigito != Character.getNumericValue(c.charAt(10))) {
            throw new DocumentoInvalidoException("CPF inválido.");
        }
    }

    /**
     * Valida um CNPJ, lançando exceção se inválido.
     * @param cnpj CNPJ com ou sem formatação
     * @throws DocumentoInvalidoException se o CNPJ for inválido
     */
    public static void validarCnpj(String cnpj) throws DocumentoInvalidoException {
        String c = limpar(cnpj);

        if (c.length() != 14) {
            throw new DocumentoInvalidoException("CNPJ deve conter 14 dígitos.");
        }

        // CNPJ com todos os dígitos iguais é inválido
        if (c.chars().distinct().count() == 1) {
            throw new DocumentoInvalidoException("CNPJ inválido.");
        }

        // Primeiro dígito verificador
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(c.charAt(i)) * pesos1[i];
        }
        int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        if (primeiroDigito != Character.getNumericValue(c.charAt(12))) {
            throw new DocumentoInvalidoException("CNPJ inválido.");
        }

        // Segundo dígito verificador
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(c.charAt(i)) * pesos2[i];
        }
        int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        if (segundoDigito != Character.getNumericValue(c.charAt(13))) {
            throw new DocumentoInvalidoException("CNPJ inválido.");
        }
    }
}
