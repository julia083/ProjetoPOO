package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;

public class RestauranteRepository {

    private static final String CAMINHO_ARQUIVO = "data/restaurante.json";

    /**
     * Salva ou substitui o restaurante cadastrado.
     */
    public void salvar(Restaurante restaurante) {
        JsonUtil.escrever(CAMINHO_ARQUIVO, restaurante);
    }

    /**
     * Retorna o restaurante cadastrado.
     * Retorna null caso não exista.
     */
    public Restaurante buscar() {
        return JsonUtil.ler(
                CAMINHO_ARQUIVO,
                Restaurante.class,
                null
        );
    }

    /**
     * Verifica se existe um restaurante cadastrado.
     */
    public boolean existeRestauranteCadastrado() {
        return buscar() != null;
    }

    public static String getCaminhoArquivo(){
        return CAMINHO_ARQUIVO;
    }
    /**
     * Atualiza os dados do restaurante.
     */
    public void atualizar(Restaurante restaurante) {
        salvar(restaurante);
    }

}