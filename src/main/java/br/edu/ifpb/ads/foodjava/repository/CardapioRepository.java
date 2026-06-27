package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.exception.ArquivoImportacaoException;
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

        // O JSON deve ter a estrutura { "cardapio": [...] } conforme o PDF [3]
        Type tipoMapa = new TypeToken<Map<String, List<ItemCardapio>>>() {}.getType();
        Map<String, List<ItemCardapio>> dados = JsonUtil.ler(caminho, tipoMapa, null);

        if (dados == null || !dados.containsKey("cardapio")) {
            throw new ArquivoImportacaoException("Estrutura inválida: chave 'cardapio' não encontrada.");
        }

        List<ItemCardapio> listaJson = dados.get("cardapio");
        int linha = 1;

        for (ItemCardapio itemNovo : listaJson) {
            try {
                // REQUISITO: Preço <= 0 deve lançar PrecoInvalidoException [4, 5]
                if (itemNovo.getPreco() <= 0) {
                    throw new PrecoInvalidoException("Preço inválido (" + itemNovo.getPreco() + ").");
                }

                // Lógica de atualização/adição para evitar duplicatas pelo nome
                ItemCardapio existente = buscarPorNome(itemNovo.getNome());
                if (existente != null) {
                    existente.setDescricao(itemNovo.getDescricao());
                    existente.setPreco(itemNovo.getPreco());
                    existente.setCategoria(itemNovo.getCategoria());
                    existente.setDisponivel(itemNovo.isDisponivel());
                    existente.setImagemPath(itemNovo.getImagemPath());
                } else {
                    this.itens.add(itemNovo);
                }

            } catch (PrecoInvalidoException e) {
                // REQUISITO: Relatório de erros linha a linha sem interromper o processo [1]
                relatorioErros.add("Linha " + linha + " (" + itemNovo.getNome() + "): " + e.getMessage());
            }
            linha++;
        }

        // Salva os dados no cardapio.json oficial do sistema [6]
        salvarTodos(this.itens);
        return relatorioErros;
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

    public void atualizar(String nomeAntigo, ItemCardapio itemAtualizado) {

        ItemCardapio itemNaLista = buscarPorNome(nomeAntigo);

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