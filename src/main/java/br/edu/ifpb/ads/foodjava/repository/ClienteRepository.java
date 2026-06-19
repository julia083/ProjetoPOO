package br.edu.ifpb.ads.foodjava.repository;


import br.edu.ifpb.ads.foodjava.exception.UsuarioDuplicadoException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.util.ValidadorCPF;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    private static final String CAMINHO = "data/clientes.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<Cliente> listarTodos() {
        Path path = Path.of(CAMINHO);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(path);
            if (json.isBlank()) {
                return new ArrayList<>();
            }
            Type tipoLista = new TypeToken<List<Cliente>>() {}.getType();
            List<Cliente> clientes = gson.fromJson(json, tipoLista);
            return clientes != null ? clientes : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler clientes.json", e);
        }
    }

    public void salvarTodos(List<Cliente> clientes) {
        try {
            Files.createDirectories(Path.of("data"));
            Files.writeString(Path.of(CAMINHO), gson.toJson(clientes));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar clientes.json", e);
        }
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return listarTodos().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public boolean existeEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }

    public boolean existeCpf(String cpf) {
        String cpfLimpo = ValidadorCPF.limpar(cpf);
        return listarTodos().stream()
                .anyMatch(c -> ValidadorCPF.limpar(c.getCpf()).equals(cpfLimpo));
    }

    public void cadastrar(Cliente cliente) throws UsuarioDuplicadoException {
        if (existeEmail(cliente.getEmail())) {
            throw new UsuarioDuplicadoException("E-mail já cadastrado: " + cliente.getEmail());
        }
        if (existeCpf(cliente.getCpf())) {
            throw new UsuarioDuplicadoException("CPF já cadastrado: " + cliente.getCpf());
        }

        List<Cliente> clientes = listarTodos();
        clientes.add(cliente);
        salvarTodos(clientes);
    }
}
