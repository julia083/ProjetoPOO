package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.exception.CancelamentoNaoPermitidoException;
import br.edu.ifpb.ads.foodjava.exception.StatusInvalidoException;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.model.enums.StatusPedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import br.edu.ifpb.ads.foodjava.util.AtualizadorAutomatico;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.exibirAlerta;

public class PainelGerenteController {

    private static final String TODOS_STATUS = "Todos";
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final ObservableList<Pedido> pedidos = FXCollections.observableArrayList();

    @FXML
    private Button avancarStatusBottom;

    @FXML
    private Button buttonGerenciarCardapio;

    @FXML
    private Button cancelarPedidoBottom;

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
    private Label txtTotalPedidos;

    @FXML
    private Label txtFaturamento;

    @FXML
    private ComboBox<String> filtroStatus;

    @FXML
    private GridPane resumoDoDiaGridPane;

    @FXML
    private Button sairBottom;

    @FXML
    private TableView<Pedido> tabelaPedidos;

    private PedidoRepository repository = new PedidoRepository();
    private AtualizadorAutomatico atualizador;

    @FXML
    void initialize() {
        configurarGridPane();
        configurarTabela();
        atualizarTabelaPedidos();
        configurarFiltroStatus();
        aplicarFiltroStatus();
        this.atualizador = new AtualizadorAutomatico(5, this::atualizarTabelaPedidos);
        this.atualizador.iniciar();
    }

    private void configurarGridPane() {
        resumoDoDiaGridPane.setHgap(20);
        resumoDoDiaGridPane.setVgap(10);

        resumoDoDiaGridPane.setStyle("-fx-padding: 15; " +
                "-fx-background-color: #ffffff; " +
                "-fx-border-color: #dddddd; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");

        resumoDoDiaGridPane.setGridLinesVisible(false);
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNome"));

        colDataHora.setCellValueFactory(cellData -> {
            var dataHora = cellData.getValue().getDataHora();
            return new ReadOnlyStringWrapper(dataHora == null ? "" : dataHora.format(FORMATO_DATA_HORA));
        });

        colTotal.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(FORMATO_MOEDA.format(cellData.getValue().calcularTotal()))
        );

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

        Pedido pedidoSelecionado = tabelaPedidos.getSelectionModel().getSelectedItem();

        if (pedidoSelecionado == null) {
            return;
        }

        try {
            int proximoOrdinal = getProximoOrdinal(pedidoSelecionado);
            StatusPedido[] todosStatus = StatusPedido.values();

            if (proximoOrdinal < todosStatus.length) {
                StatusPedido proximoStatus = todosStatus[proximoOrdinal];

                if (proximoStatus == StatusPedido.CANCELADO) {
                    throw new StatusInvalidoException(
                            "Fluxo de atendimento finalizado. Não é possível avançar além de ENTREGUE."
                    );
                }

                pedidoSelecionado.setStatus(proximoStatus);

                repository.atualizar(pedidoSelecionado);
                atualizarTabelaPedidos();
            }

        } catch (StatusInvalidoException e) {
            exibirAlerta("Erro ao Avançar Status", "Mudança de Status Inválida", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void cancelarPedido(ActionEvent event) {
        Pedido pedidoSelecionado = tabelaPedidos.getSelectionModel().getSelectedItem();

        if (pedidoSelecionado == null) {
            exibirAlerta("Erro", "Nenhum pedido foi selecionado", "Selecione um pedido para cancelar.", Alert.AlertType.WARNING);
            return;
        }
        try{
        StatusPedido statusAtual = pedidoSelecionado.getStatus();

        if (statusAtual != StatusPedido.AGUARDANDO_CONFIRMACAO) {
            throw new CancelamentoNaoPermitidoException(
                    "Não é possível cancelar um pedido que já foi confirmado ou está em preparo.");
        }

        pedidoSelecionado.setStatus(StatusPedido.CANCELADO);

        repository.atualizar(pedidoSelecionado);
        atualizarTabelaPedidos();}
        catch(CancelamentoNaoPermitidoException e){
            exibirAlerta("Erro ao cancelar", "Cancelamento não permitido", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void gerenciarCardapio(ActionEvent event){
        if (atualizador!=null){
            atualizador.parar();
        }
        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gerenciar-cardapio.fxml"));
            Parent root = loader.load();

            // 2. Pega a janela (Stage) atual a partir do botão que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Define a nova cena na mesma janela
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void voltarTelaLogin(ActionEvent event) throws IOException{
        if (atualizador!=null){
            atualizador.parar();
        }
        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // 2. Pega a janela (Stage) atual a partir do botão que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Define a nova cena na mesma janela
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void atualizarTabelaPedidos() {
        List<Pedido> listaPedidos = repository.listarTodos();

        pedidos.setAll(listaPedidos);

        atualizarResumoDoDia(listaPedidos);

        aplicarFiltroStatus();

        tabelaPedidos.refresh();
    }

    private void atualizarResumoDoDia(List<Pedido> pedidos) {

        int totalPedidos = pedidos.size();

        double faturamento = pedidos.stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .mapToDouble(Pedido::calcularTotal)
                .sum();

        txtTotalPedidos.setText(String.valueOf(totalPedidos));
        txtFaturamento.setText(FORMATO_MOEDA.format(faturamento));
    }

    private static int getProximoOrdinal(Pedido pedidoSelecionado) {
        StatusPedido statusAtual = pedidoSelecionado.getStatus();
        if (statusAtual == null || statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new br.edu.ifpb.ads.foodjava.exception.StatusInvalidoException(
                    "Não é possível avançar o status de um pedido que já está " + statusAtual + "."
            );
        }
        int proximoOrdinal = statusAtual.ordinal() + 1;
        return proximoOrdinal;
    }
}