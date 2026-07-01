package br.edu.ifpb.ads.foodjava.repository;

import br.edu.ifpb.ads.foodjava.exception.ArquivoImportacaoException;
import br.edu.ifpb.ads.foodjava.exception.CategoriaInvalidaException;
import br.edu.ifpb.ads.foodjava.exception.PrecoInvalidoException;
import br.edu.ifpb.ads.foodjava.interfaces.Repositorio;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardapioRepository implements Repositorio<ItemCardapio> {

    private final RestauranteRepository restauranteRepository;
    private List<ItemCardapio> itens;

    public CardapioRepository() {
        this.restauranteRepository = new RestauranteRepository();
        this.itens = listarTodos();
    }

    public void salvar(ItemCardapio item) {
        if (item == null || !item.validar()) {
            throw new IllegalArgumentException("Item do cardapio invalido ou incompleto.");
        }

        itens.add(item);
        salvarTodos(itens);
    }

    public List<String> importarCardapio(String caminho) throws ArquivoImportacaoException {
        List<String> relatorioErros = new ArrayList<>();

        Type tipoMapa = new TypeToken<Map<String, List<ItemCardapio>>>() {}.getType();
        Map<String, List<ItemCardapio>> dados = JsonUtil.ler(caminho, tipoMapa, null);

        if (dados == null || !dados.containsKey("cardapio") || dados.get("cardapio") == null) {
            throw new ArquivoImportacaoException("Estrutura invalida: chave 'cardapio' nao encontrada.");
        }

        List<ItemCardapio> listaJson = dados.get("cardapio");
        int linha = 1;

        for (ItemCardapio itemNovo : listaJson) {
            try {
                validarItemImportado(itemNovo);

                ItemCardapio existente = null;
                if (itemNovo.getItemID() != null) {
                    existente = buscarPorId(itemNovo.getItemID());
                }

                if (existente == null && itemNovo.getNome() != null) {
                    existente = buscarPorNome(itemNovo.getNome());
                }

                if (existente != null) {
                    existente.setNome(itemNovo.getNome());
                    existente.setDescricao(itemNovo.getDescricao());
                    existente.setPreco(itemNovo.getPreco());
                    existente.setCategoria(itemNovo.getCategoria());
                    existente.setDisponivel(itemNovo.isDisponivel());
                    existente.setImagemPath(itemNovo.getImagemPath());
                } else {
                    ItemCardapio novoItemComId = new ItemCardapio();

                    novoItemComId.setNome(itemNovo.getNome());
                    novoItemComId.setDescricao(itemNovo.getDescricao());
                    novoItemComId.setPreco(itemNovo.getPreco());
                    novoItemComId.setCategoria(itemNovo.getCategoria());
                    novoItemComId.setDisponivel(itemNovo.isDisponivel());
                    novoItemComId.setImagemPath(itemNovo.getImagemPath());

                    this.itens.add(novoItemComId);
                }

            } catch (PrecoInvalidoException | CategoriaInvalidaException | IllegalArgumentException e) {
                String nomeItem = itemNovo != null && itemNovo.getNome() != null ? itemNovo.getNome() : "Item sem nome";
                relatorioErros.add("Linha " + linha + " (" + nomeItem + "): " + e.getMessage());
            }
            linha++;
        }

        salvarTodos(this.itens);
        return relatorioErros;
    }

    public ItemCardapio buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        return itens.stream()
                .filter(item -> item != null
                        && item.getNome() != null
                        && item.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    public ItemCardapio buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        return itens.stream()
                .filter(item -> item != null && id.equals(item.getItemID()))
                .findFirst()
                .orElse(null);
    }

    public void deletar(String id) {
        if (id != null && !id.isBlank()) {
            itens.removeIf(item -> item != null && id.equals(item.getItemID()));
            salvarTodos(itens);
        }
    }

    public List<ItemCardapio> buscarSomenteDisponiveis() {
        return itens.stream()
                .filter(item -> item != null && item.isDisponivel())
                .collect(Collectors.toList());
    }

    public void atualizar(ItemCardapio itemAtualizado) {
        if (itemAtualizado == null || !itemAtualizado.validar()) {
            throw new IllegalArgumentException("Item do cardapio invalido ou incompleto.");
        }

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
        Restaurante restaurante = restauranteRepository.buscar();

        if (restaurante == null) {
            this.itens = new ArrayList<>();
            return new ArrayList<>();
        }

        this.itens = new ArrayList<>(restaurante.getCardapio());
        return new ArrayList<>(this.itens);
    }

    @Override
    public void salvarTodos(List<ItemCardapio> lista) {
        Restaurante restaurante = restauranteRepository.buscar();

        if (restaurante == null) {
            throw new IllegalStateException("Restaurante nao configurado.");
        }

        this.itens = lista == null ? new ArrayList<>() : new ArrayList<>(lista);
        restaurante.setCardapio(this.itens);
        restauranteRepository.salvar(restaurante);
    }

    private void validarItemImportado(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("Item vazio.");
        }
        if (item.getNome() == null || item.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome nao informado.");
        }
        if (item.getPreco() <= 0) {
            throw new PrecoInvalidoException("Preco invalido (" + item.getPreco() + ").");
        }
        if (item.getCategoria() == null) {
            throw new CategoriaInvalidaException("Categoria invalida ou nao informada.");
        }
    }
}
