package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.model.enums.StatusPedido;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PainelGerenteController {

    private static final String TODOS_STATUS = "Todos";
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final ObservableList<Pedido> pedidos = FXCollections.observableArrayList();

    @FXML
    private Button avancarStatusBottom;

    @FXML
    private Button cancelarPedidoBottom;

    @FXML
    private Button confirmarPedidoBottom;

    @FXML
    private TableColumn<Pedido, String> colCliente;

    @FXML
    private TableColumn<Pedido, String> colDataHora;

    @FXML
    private TableColumn<Pedido, String> colId;

    @FXML
    private TableColumn<Pedido, String> colStatus;

    @FXML
    private TableColumn<Pedido, String> colTotal;

    @FXML
    private ComboBox<String> filtroStatus;

    @FXML
    private GridPane resumoDoDiaGridPane;

    @FXML
    private Button sairBottom;

    @FXML
    private TableView<Pedido> tabelaPedidos;

    @FXML
    private Button verDetalhesBottom;

    @FXML
    void initialize() {
        configurarTabela();
        configurarFiltroStatus();
        aplicarFiltroStatus();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId()));
        colCliente.setCellValueFactory(cellData -> {
            var cliente = cellData.getValue().getCliente();
            return new ReadOnlyStringWrapper(cliente == null ? "" : cliente.getNome());
        });
        colDataHora.setCellValueFactory(cellData -> {
            var dataHora = cellData.getValue().getDataHora();
            return new ReadOnlyStringWrapper(dataHora == null ? "" : dataHora.format(FORMATO_DATA_HORA));
        });
        colTotal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(FORMATO_MOEDA.format(cellData.getValue().calcularTotal())));
        colStatus.setCellValueFactory(cellData -> {
            var status = cellData.getValue().getStatus();
            return new ReadOnlyStringWrapper(status == null ? "" : status.name());
        });
    }

    private void configurarFiltroStatus() {
        filtroStatus.getItems().setAll(TODOS_STATUS);
        for (StatusPedido status : StatusPedido.values()) {
            filtroStatus.getItems().add(status.name());
        }

        filtroStatus.getSelectionModel().select(TODOS_STATUS);
        filtroStatus.valueProperty().addListener((observable, valorAnterior, valorAtual) -> aplicarFiltroStatus());
    }

    private void aplicarFiltroStatus() {
        String statusSelecionado = filtroStatus.getValue();

        if (statusSelecionado == null || TODOS_STATUS.equals(statusSelecionado)) {
            tabelaPedidos.setItems(pedidos);
            return;
        }

        StatusPedido status = StatusPedido.valueOf(statusSelecionado);
        ObservableList<Pedido> pedidosFiltrados = pedidos.filtered(pedido -> pedido.getStatus() == status);
        tabelaPedidos.setItems(pedidosFiltrados);
    }

    @FXML
    void avancarStatus(ActionEvent event) {

    }

    @FXML
    void cancelarPedido(ActionEvent event) {

    }

    @FXML
    void confirmarPedido(ActionEvent event) {

    }

    @FXML
    void verDetalhes(ActionEvent event) {

    }

    @FXML
    void voltarTelaLogin(ActionEvent event) {

    }

}
