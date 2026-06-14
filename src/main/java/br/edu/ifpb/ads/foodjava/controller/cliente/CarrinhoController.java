package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.controller.autenticacao.LoginController;
import br.edu.ifpb.ads.foodjava.exception.CarrinhoVazioException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import br.edu.ifpb.ads.foodjava.util.GeradorID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CarrinhoController {

    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final ObservableList<ItemPedido> itensCarrinho = FXCollections.observableArrayList();
    private Cliente cliente;

    @FXML
    private ListView<ItemPedido> listaCarrinho;

    @FXML
    private Label totalLabel;

    @FXML
    void initialize() {
        configurarLista();
        atualizarCarrinho();
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

    @FXML
    void limparCarrinho(ActionEvent event) {
        if (CardapioController.getCarrinho().isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho vazio", "Nao ha itens para remover.");
            return;
        }

        CardapioController.limparCarrinho();
        atualizarCarrinho();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Carrinho limpo", "Todos os itens foram removidos do carrinho.");
    }

    @FXML
    void confirmarPedido(ActionEvent event) {
        try {
            if (cliente == null) {
                cliente = CardapioController.getClienteLogado();
            }

            Pedido pedido = new Pedido(GeradorID.gerar(), cliente);
            pedido.setItens(new ArrayList<>(CardapioController.getCarrinho()));
            pedido.confirmar();

            if (!pedido.validar()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Pedido invalido", "Nao foi possivel identificar o cliente do pedido.");
                return;
            }

            PedidoRepository.salvar(pedido);
            CardapioController.limparCarrinho();
            atualizarCarrinho();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Pedido confirmado",
                    "Pedido enviado com sucesso. Total: " + FORMATO_MOEDA.format(pedido.calcularTotal()));
            voltarCardapio(event);
        } catch (CarrinhoVazioException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Carrinho vazio", e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Pedido confirmado, mas nao foi possivel voltar ao cardapio.");
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
