package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SenhaUtil {

    /**
     * Gera o hash SHA-256 da senha informada.
     * Nunca armazene a senha original — sempre use o hash.
     */
    public static String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de senha.", e);
        }
    }

    /**
     * Verifica se a senha digitada corresponde ao hash salvo.
     * Usada no login.
     */
    public static boolean verificar(String senhaDigitada, String hashSalvo) {
        return hash(senhaDigitada).equals(hashSalvo);
    }

    /**
     * Valida as regras da senha:
     * - mínimo 8 caracteres
     * - pelo menos um dígito numérico
     * Lança SenhaInvalidaException se alguma regra for violada.
     * Usada no cadastro.
     */
    public static void senhaValida(String senha) throws SenhaInvalidaException {
        if (senha == null || senha.length() < 8) {
            throw new SenhaInvalidaException("Senha deve ter no mínimo 8 caracteres.");
        }
        if (senha.chars().noneMatch(Character::isDigit)) {
            throw new SenhaInvalidaException("Senha deve conter pelo menos um dígito numérico.");
        }
    }
}
