package br.edu.ifpb.ads.foodjava.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RestauranteRepository {

    private static final String CAMINHO_ARQUIVO = "FoodJava/data/restaurante.json/";

    public boolean existeRestauranteCadastrado() {
        File arquivo = new File(CAMINHO_ARQUIVO);

        // Se o arquivo nem existe, certamente não há restaurantes
        if (!arquivo.exists()) {
            return false;
        }

        try {
            String conteudo = new String(Files.readAllBytes(Paths.get(CAMINHO_ARQUIVO)));

            // Verificação simples: se o JSON estiver vazio, for apenas um array vazio [] ou objeto vazio {}
            if (conteudo.trim().isEmpty() || conteudo.equals("[]") || conteudo.equals("{}")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}