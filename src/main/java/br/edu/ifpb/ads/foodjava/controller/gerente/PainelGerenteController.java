package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.exception.CancelamentoNaoPermitidoException;
import br.edu.ifpb.ads.foodjava.exception.StatusInvalidoException;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.model.enums.StatusPedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.AtualizadorAutomatico;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    private final PedidoRepository repository = new PedidoRepository();

    @FXML
    private Button avancarStatusBottom;

    @FXML
    private Button buttonGerenciarCardapio;

    @FXML
    private Button buttonEditarRestaurante;

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
    private ImageView logoTipoView;

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

    private AtualizadorAutomatico atualizador;

    @FXML
    void initialize() {
        ImagemUtil.carregarLogoDoJson(logoTipoView, RestauranteRepository.getCaminhoArquivo());
        configurarGridPane();
        configurarTabela();
        atualizarTabelaPedidos();
        configurarFiltroStatus();
        aplicarFiltroStatus();
        this.atualizador = new AtualizadorAutomatico(7, this::atualizarTabelaPedidos);
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
            pedidoSelecionado.avancarStatus();
            repository.atualizar(pedidoSelecionado);
            atualizarTabelaPedidos();
        } catch (StatusInvalidoException e) {
            exibirAlerta("Erro ao avancar status", "Mudanca de status invalida", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void cancelarPedido(ActionEvent event) {
        Pedido pedidoSelecionado = tabelaPedidos.getSelectionModel().getSelectedItem();

        if (pedidoSelecionado == null) {
            exibirAlerta("Erro", "Nenhum pedido foi selecionado", "Selecione um pedido para cancelar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            pedidoSelecionado.cancelar();
            repository.atualizar(pedidoSelecionado);
            atualizarTabelaPedidos();
        } catch (CancelamentoNaoPermitidoException e) {
            exibirAlerta("Erro ao cancelar", "Cancelamento nao permitido", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void gerenciarCardapio(ActionEvent event) {
        pararAtualizador();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gerenciar-cardapio.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void editarRestaurante(ActionEvent event) {
        pararAtualizador();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar-restaurante.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void voltarTelaLogin(ActionEvent event) throws IOException {
        pararAtualizador();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
        atualizarResumoDoDia();
        aplicarFiltroStatus();
        tabelaPedidos.refresh();
    }

    private void atualizarResumoDoDia() {
        txtTotalPedidos.setText(String.valueOf(repository.contarPedidosDoDia()));
        txtFaturamento.setText(FORMATO_MOEDA.format(repository.calcularFaturamentoDoDia()));
    }

    private void pararAtualizador() {
        if (atualizador != null) {
            atualizador.parar();
        }
    }
}
