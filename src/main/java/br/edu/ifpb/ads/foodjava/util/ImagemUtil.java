package br.edu.ifpb.ads.foodjava.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import br.edu.ifpb.ads.foodjava.model.Restaurante;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImagemUtil {

    private static final String PASTA_UPLOADS = "uploads";
    private static final String IMAGEM_PADRAO = "/images/placeholder.png";

    /**
     * Copia a imagem escolhida pelo usuário para a pasta uploads/ do projeto
     * e devolve o caminho relativo a ser salvo no JSON.
     */
    public static String salvar(File arquivoOrigem) {
        try {
            Files.createDirectories(Path.of(PASTA_UPLOADS));

            String extensao = obterExtensao(arquivoOrigem.getName());
            String nomeUnico = GeradorID.gerar() + extensao;

            Path destino = Path.of(PASTA_UPLOADS, nomeUnico);
            Files.copy(arquivoOrigem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            return PASTA_UPLOADS + "/" + nomeUnico;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem: " + arquivoOrigem.getName(), e);
        }
    }

    /**
     * Carrega uma Image a partir do caminho relativo salvo no JSON.
     * Se o caminho for nulo, vazio, ou o arquivo não existir, devolve a imagem padrão.
     */
    public static Image carregar(String caminhoRelativo) {
        if (caminhoRelativo == null || caminhoRelativo.isBlank()) {
            return carregarPlaceholder();
        }

        File arquivo = new File(caminhoRelativo);
        if (!arquivo.exists()) {
            return carregarPlaceholder();
        }

        return new Image(arquivo.toURI().toString());
    }

    /**
     * Lê o JSON do restaurante e define a logo no ImageView com segurança.
     */
    public static void carregarLogoDoJson(ImageView imageView, String caminhoJson) {
        if (imageView == null) return;

        try {
            // 1. Lê o objeto do JSON
            Restaurante rest = JsonUtil.ler(caminhoJson, Restaurante.class, null);

            // 2. Valida se o restaurante e o atributo da logo existem
            if (rest != null && rest.getLogotipoPath() != null && !rest.getLogotipoPath().isBlank()) {

                // 3. IGUAL AO SEU TESTE: Passa o caminho por um objeto File para corrigir as barras
                java.io.File arquivo = new java.io.File(rest.getLogotipoPath());

                if (arquivo.exists()) {
                    // Aplica a imagem usando a URI que funcionou no seu teste
                    imageView.setImage(new Image(arquivo.toURI().toString()));
                    return; // Sucesso, pode sair do método
                }
            }

            // Se cair aqui (não achou no json ou arquivo não existe), limpa a tela
            imageView.setImage(null);

        } catch (Exception e) {
            System.err.println("Erro ao carregar logo do JSON: " + e.getMessage());
            imageView.setImage(null);
        }
    }

    private static Image carregarPlaceholder() {
        return new Image(ImagemUtil.class.getResourceAsStream(IMAGEM_PADRAO));
    }

    private static String obterExtensao(String nomeArquivo) {
        int ponto = nomeArquivo.lastIndexOf('.');
        return ponto >= 0 ? nomeArquivo.substring(ponto) : "";
    }
}