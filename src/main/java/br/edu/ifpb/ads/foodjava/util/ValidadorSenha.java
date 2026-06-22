package br.edu.ifpb.ads.foodjava.util;

public class ValidadorSenha {
    public static boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 8 && senha.chars().anyMatch(Character::isDigit);
    }
}