package br.edu.ifpb.ads.foodjava.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    if (value == null) out.nullValue();
                    else out.value(FORMATTER.format(value));
                }

                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    return LocalDateTime.parse(in.nextString(), FORMATTER);
                }
            })
            .create();

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
                return valorPadrao;
            }

            T resultado = gson.fromJson(json, tipo);
            return resultado != null ? resultado : valorPadrao;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + caminho, e);
        }
    }

    /**
     * Converte o objeto para JSON e grava no arquivo informado.
     * Cria a pasta automaticamente, se não existir.
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