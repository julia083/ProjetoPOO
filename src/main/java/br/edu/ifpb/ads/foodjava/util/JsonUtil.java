package br.edu.ifpb.ads.foodjava.util;

import br.edu.ifpb.ads.foodjava.exception.ArquivoImportacaoException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Lê um arquivo JSON e converte para o tipo informado.
     * Se o arquivo não existir ou estiver vazio, devolve o valorPadrao.
     */
    public static <T> T ler(String caminho, Type tipo, T valorPadrao) {
        Path path = Path.of(caminho);

        if (!Files.exists(path)) {
            return valorPadrao;
        }

        try {
            String json = Files.readString(path);
            if (json.isBlank()) {
                // Em vez de retornar valorPadrao, o projeto exige lançar a exceção de importação
                throw new ArquivoImportacaoException("O arquivo de importação está vazio.");
            }

            T resultado = gson.fromJson(json, tipo);
            if (resultado == null) {
                throw new ArquivoImportacaoException("Estrutura JSON inválida.");
            }
            return resultado;

        } catch (IOException e) {
            // Troque a RuntimeException pela exceção exigida no projeto
            throw new ArquivoImportacaoException("Arquivo JSON de importação ausente ou inacessível.");
        }
    }

    /**
     * Converte o objeto para JSON e grava no arquivo informado.
     * Cria a pasta "data" automaticamente, se não existir.
     */
    public static void escrever(String caminho, Object dados) {
        try {
            Path path = Path.of(caminho);
            Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(dados));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + caminho, e);
        }
    }
}
