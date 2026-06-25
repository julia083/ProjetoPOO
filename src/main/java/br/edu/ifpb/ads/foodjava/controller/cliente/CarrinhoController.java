package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.controller.autenticacao.LoginController;
import br.edu.ifpb.ads.foodjava.exception.CarrinhoVazioException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import br.edu.ifpb.ads.foodjava.util.GeradorID;
import br.edu.ifpb.ads.foodjava.util.Mensagem;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CarrinhoController {

    private final PedidoRepository pedidoRepository = new PedidoRepository();
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final ObservableList<ItemPedido> itensCarrinho = FXCollections.observableArrayList();
    private Cliente cliente;

    @FXML
    private ListView<ItemPedido> listaCarrinho;

    @FXML
    private Label totalLabel;

    @FXML
    private Button removerItemButton; // Novo botão

    @FXML
    private Button limparCarrinhoButton;

    @FXML
    private Button confirmarPedidoButton;

    @FXML
    void initialize() {
        configurarLista();
        atualizarCarrinho();

        // Habilita/desabilita o botão "Remover Item" conforme seleção
        removerItemButton.disableProperty().bind(
                Bindings.isEmpty(listaCarrinho.getSelectionModel().getSelectedItems())
        );
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    private void configurarLista() {
        listaCarrinho.setItems(itensCarrinho);
        listaCarrinho.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ItemPedido itemPedido, boolean empty) {
                super.updateItem(itemPedido, empty);

                if (empty || itemPedido == null || itemPedido.getItemCardapio() == null) {
                    setText(null);
                    return;
                }

                String nome = itemPedido.getItemCardapio().getNome();
                int quantidade = itemPedido.getQuantidade();
                String precoUnitario = FORMATO_MOEDA.format(itemPedido.getItemCardapio().getPreco());
                String subtotal = FORMATO_MOEDA.format(itemPedido.calcularSubtotal());

                setText(String.format("%dx %s - %s un. | Subtotal: %s",
                        quantidade, nome, precoUnitario, subtotal));
            }
        });

        // Seleção única (padrão já é single)
        listaCarrinho.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
    }

    private void atualizarCarrinho() {
        itensCarrinho.setAll(CardapioController.getCarrinho());
        totalLabel.setText(FORMATO_MOEDA.format(calcularTotal()));
    }

    private double calcularTotal() {
        return CardapioController.getCarrinho().stream()
                .mapToDouble(ItemPedido::calcularSubtotal)
                .sum();
    }

    // === NOVO MÉTODO: Remover item selecionado ===
    @FXML
    void removerItemSelecionado(ActionEvent event) {
        ItemPedido selecionado = listaCarrinho.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Mensagem.mostrarAlerta("Nenhum item selecionado", "Selecione um item para remover.");
            return;
        }

        // Remove da lista observável
        itensCarrinho.remove(selecionado);

        // Remove da lista estática do CardapioController
        CardapioController.getCarrinho().remove(selecionado);

        // Atualiza total
        atualizarTotal();

        Mensagem.mostrarAlerta("Item removido", selecionado.getItemCardapio().getNome() + " foi removido do carrinho.");
    }

    // Atualiza apenas o total (sem recarregar a lista toda)
    private void atualizarTotal() {
        totalLabel.setText(FORMATO_MOEDA.format(calcularTotal()));
    }

    @FXML
    void limparCarrinho(ActionEvent event) {
        if (CardapioController.getCarrinho().isEmpty()) {
            Mensagem.mostrarAlerta("Carrinho vazio", "Não há itens para remover.");
            return;
        }

        CardapioController.limparCarrinho();
        atualizarCarrinho();
        Mensagem.mostrarAlerta("Carrinho limpo", "Todos os itens foram removidos do carrinho.");
    }

    @FXML
    void confirmarPedido(ActionEvent event) {
        try {
            if (cliente == null) {
                cliente = CardapioController.getClienteLogado();
            }

            if (CardapioController.getCarrinho().isEmpty()) {
                Mensagem.mostrarAlerta("Carrinho vazio", "Adicione itens antes de confirmar o pedido.");
                return;
            }

            Pedido pedido = new Pedido(GeradorID.gerar(), cliente);
            pedido.setItens(new ArrayList<>(CardapioController.getCarrinho()));

            pedidoRepository.adicionar(pedido);

            CardapioController.limparCarrinho();
            atualizarCarrinho();

            Mensagem.mostrarAlerta("Pedido confirmado",
                    "Pedido enviado com sucesso! Total: " + FORMATO_MOEDA.format(pedido.calcularTotal()));

            voltarCardapio(event);

        } catch (CarrinhoVazioException e) {
            Mensagem.mostrarAlerta("Carrinho vazio", e.getMessage());
        } catch (IOException e) {
            Mensagem.mostrarAlerta("Erro", "Pedido confirmado, mas não foi possível voltar ao cardápio.");
        }
    }

    @FXML
    void voltarCardapio(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardapio.fxml"));
        Parent root = loader.load();

        CardapioController controller = loader.getController();
        controller.setClienteLogado(cliente == null ? CardapioController.getClienteLogado() : cliente);
        controller.setRestaurante(LoginController.getRestaurante());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Cardapio");
        stage.show();
    }
}