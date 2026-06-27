package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.exception.ArquivoImportacaoException;
import br.edu.ifpb.ads.foodjava.exception.CategoriaInvalidaException;
import br.edu.ifpb.ads.foodjava.exception.PrecoInvalidoException;
import br.edu.ifpb.ads.foodjava.interfaces.Repositorio;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public List<String> importarCardapio(String caminho) throws ArquivoImportacaoException {
        List<String> relatorioErros = new ArrayList<>();

        Type tipoMapa = new TypeToken<Map<String, List<ItemCardapio>>>() {}.getType();
        Map<String, List<ItemCardapio>> dados = JsonUtil.ler(caminho, tipoMapa, null);

        if (dados == null || !dados.containsKey("cardapio")) {
            throw new ArquivoImportacaoException("Estrutura inválida: chave 'cardapio' não encontrada.");
        }

        List<ItemCardapio> listaJson = dados.get("cardapio");
        int linha = 1;

        for (ItemCardapio itemNovo : listaJson) {
            try {
                // 1. REQUISITO: Preço <= 0 deve lançar PrecoInvalidoException
                if (itemNovo.getPreco() <= 0) {
                    throw new PrecoInvalidoException("Preço inválido (" + itemNovo.getPreco() + ").");
                }

                if (itemNovo.getCategoria() == null) {
                    throw new CategoriaInvalidaException("Categoria inválida ou não informada.");
                }

                ItemCardapio existente = null;
                if (itemNovo.getItemID() != null) {
                    existente = buscarPorId(itemNovo.getItemID());
                }

                if (existente == null && itemNovo.getNome() != null) {
                    existente = buscarPorNome(itemNovo.getNome());
                }

                if (existente != null) {
                    // Atualiza o item que já existe no sistema
                    existente.setDescricao(itemNovo.getDescricao());
                    existente.setPreco(itemNovo.getPreco());
                    existente.setCategoria(itemNovo.getCategoria());
                    existente.setDisponivel(itemNovo.isDisponivel());
                    existente.setImagemPath(itemNovo.getImagemPath());
                } else {
                    // Instancia um novo objeto seguro com ID automático
                    ItemCardapio novoItemComId = new ItemCardapio();

                    novoItemComId.setNome(itemNovo.getNome());
                    novoItemComId.setDescricao(itemNovo.getDescricao());
                    novoItemComId.setPreco(itemNovo.getPreco());
                    novoItemComId.setCategoria(itemNovo.getCategoria());
                    novoItemComId.setDisponivel(itemNovo.isDisponivel());
                    novoItemComId.setImagemPath(itemNovo.getImagemPath());

                    this.itens.add(novoItemComId);
                }

            } catch (PrecoInvalidoException | CategoriaInvalidaException e) {
                String nomeItem = itemNovo.getNome() != null ? itemNovo.getNome() : "Item sem nome";
                relatorioErros.add("Linha " + linha + " (" + nomeItem + "): " + e.getMessage());
            }
            linha++;
        }

        salvarTodos(this.itens);
        return relatorioErros;
    }

    // Busca um item pelo nome
    public ItemCardapio buscarPorNome(String nome) {
        return itens.stream()
                .filter(item -> item.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }
    public ItemCardapio buscarPorId(String id) {
        if (id == null) return null;
        return itens.stream()
                .filter(item -> id.equals(item.getItemID()))
                .findFirst()
                .orElse(null);
    }

    public void deletar(String id) {
        if (id != null) {
            itens.removeIf(item -> id.equals(item.getItemID()));
            salvarTodos(itens);
        }
    }

    // Retorna apenas itens disponíveis
    public List<ItemCardapio> buscarSomenteDisponiveis() {
        return itens.stream()
                .filter(ItemCardapio::isDisponivel)
                .collect(Collectors.toList());
    }

    public void atualizar(ItemCardapio itemAtualizado) {
        if (itemAtualizado == null) return;

        ItemCardapio itemNaLista = buscarPorId(itemAtualizado.getItemID());

        if (itemNaLista != null) {
            itemNaLista.setNome(itemAtualizado.getNome());
            itemNaLista.setDescricao(itemAtualizado.getDescricao());
            itemNaLista.setPreco(itemAtualizado.getPreco());
            itemNaLista.setCategoria(itemAtualizado.getCategoria());
            itemNaLista.setDisponivel(itemAtualizado.isDisponivel());
            itemNaLista.setImagemPath(itemAtualizado.getImagemPath());
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