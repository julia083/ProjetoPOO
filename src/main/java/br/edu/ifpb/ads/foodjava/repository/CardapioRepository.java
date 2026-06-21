package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.interfaces.Repositorio;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardapioRepository implements Repositorio<ItemCardapio> {

    private static final String CAMINHO_ARQUIVO = "data/cardapio.json";

    private List<ItemCardapio> itens;

    public CardapioRepository() {
        this.itens = listarTodos();
    }

    public void salvar(ItemCardapio item) {
        itens.add(item);
        salvarTodos(itens);
    }

    public void importarCardapio(String caminhoArquivoImportacao) {
        Type tipoLista = new TypeToken<ArrayList<ItemCardapio>>() {}.getType();
        List<ItemCardapio> itensImportados = JsonUtil.ler(caminhoArquivoImportacao, tipoLista, new ArrayList<>());

        for (ItemCardapio itemImportado : itensImportados) {
            ItemCardapio itemAntigo = buscarPorNome(itemImportado.getNome());
            if (itemAntigo != null) {
                itemAntigo.setDescricao(itemImportado.getDescricao());
                itemAntigo.setPreco(itemImportado.getPreco());
                itemAntigo.setCategoria(itemImportado.getCategoria());
                itemAntigo.setDisponivel(itemImportado.isDisponivel());
                itemAntigo.setImagemPath(itemImportado.getImagemPath());
            } else {
                itens.add(itemImportado);
            }
        }
        // Salva no disco uma única vez no final
        salvarTodos(itens);
    }

    // Retorna tudo o que está cadastrado
    public List<ItemCardapio> buscarTodos() {
        return new ArrayList<>(itens);
    }

    // Busca um item pelo nome
    public ItemCardapio buscarPorNome(String nome) {
        return itens.stream()
                .filter(item -> item.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    // Remove um item pelo nome
    public void deletar(String nome) {
        itens.removeIf(item -> item.getNome().equalsIgnoreCase(nome));
        salvarTodos(itens);
    }

    // Filtra itens por categoria
    public List<ItemCardapio> buscarPorCategoria(Categoria categoria) {
        return itens.stream()
                .filter(item -> item.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    // Retorna apenas itens disponíveis
    public List<ItemCardapio> buscarSomenteDisponiveis() {
        return itens.stream()
                .filter(ItemCardapio::isDisponivel)
                .collect(Collectors.toList());
    }

    // Atualiza um item existente
    public void atualizar(ItemCardapio itemAtualizado) {

        ItemCardapio itemAntigo = buscarPorNome(itemAtualizado.getNome());

        if (itemAntigo != null) {
            itemAntigo.setDescricao(itemAtualizado.getDescricao());
            itemAntigo.setPreco(itemAtualizado.getPreco());
            itemAntigo.setCategoria(itemAtualizado.getCategoria());
            itemAntigo.setDisponivel(itemAtualizado.isDisponivel());
            itemAntigo.setImagemPath(itemAtualizado.getImagemPath());
        } else {
            itens.add(itemAtualizado);
        }

        salvarTodos(itens);
    }

    @Override
    public List<ItemCardapio> listarTodos() {

        Type tipoLista = new TypeToken<ArrayList<ItemCardapio>>() {
        }.getType();

        return JsonUtil.ler(
                CAMINHO_ARQUIVO,
                tipoLista,
                new ArrayList<>()
        );
    }

    @Override
    public void salvarTodos(List<ItemCardapio> lista) {
        JsonUtil.escrever(CAMINHO_ARQUIVO, lista);
    }
}