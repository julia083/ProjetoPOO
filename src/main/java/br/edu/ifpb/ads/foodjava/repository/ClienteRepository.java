package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.exception.UsuarioDuplicadoException;
import br.edu.ifpb.ads.foodjava.interfaces.Repositorio;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.util.DocumentoUtil;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository implements Repositorio<Cliente> {

    private static final String CAMINHO = "data/clientes.json";
    private static final Type TIPO_LISTA = new TypeToken<List<Cliente>>() {}.getType();

    @Override
    public List<Cliente> listarTodos() {
        return JsonUtil.ler(CAMINHO, TIPO_LISTA, new ArrayList<>());
    }

    @Override
    public void salvarTodos(List<Cliente> clientes) {
        JsonUtil.escrever(CAMINHO, clientes);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        String emailNormalizado = email.trim();
        return listarTodos().stream()
                .filter(c -> c != null
                        && c.getEmail() != null
                        && c.getEmail().equalsIgnoreCase(emailNormalizado))
                .findFirst();
    }

    public boolean existeEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }

    public boolean existeCpf(String cpf) {
        String cpfLimpo = DocumentoUtil.limpar(cpf);
        if (cpfLimpo.isBlank()) {
            return false;
        }

        return listarTodos().stream()
                .filter(c -> c != null)
                .anyMatch(c -> DocumentoUtil.limpar(c.getCpf()).equals(cpfLimpo));
    }

    public void cadastrar(Cliente cliente) throws UsuarioDuplicadoException {
        if (cliente == null || !cliente.validar()) {
            throw new IllegalArgumentException("Cliente invalido ou incompleto.");
        }

        DocumentoUtil.validarCpf(cliente.getCpf());

        if (existeEmail(cliente.getEmail())) {
            throw new UsuarioDuplicadoException("E-mail ja cadastrado: " + cliente.getEmail());
        }
        if (existeCpf(cliente.getCpf())) {
            throw new UsuarioDuplicadoException("CPF ja cadastrado: " + cliente.getCpf());
        }

        List<Cliente> clientes = listarTodos();
        clientes.add(cliente);
        salvarTodos(clientes);
    }
}
