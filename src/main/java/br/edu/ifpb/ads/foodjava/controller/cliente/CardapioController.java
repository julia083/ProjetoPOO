package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.repository.CardapioRepository;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.AtualizadorAutomatico;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardapioController {

    private static final double CARD_WIDTH = 200;
    private static final double CARD_GAP = 15;

    @FXML private Button carrinhoButton;
    @FXML private GridPane entradaGrid, principalGrid, sobremesaGrid, bebidasGrid;
    @FXML private ImageView logoTipoView;

    private Restaurante restaurante;
    private static Cliente clienteLogado;
    private static final List<ItemPedido> carrinho = new ArrayList<>();
    private List<ItemCardapio> itensDisponiveis = new ArrayList<>();
    private AtualizadorAutomatico atualizador;

    @FXML
    public void initialize() {
        ImagemUtil.carregarLogoDoJson(logoTipoView, RestauranteRepository.getCaminhoArquivo());
        configurarResponsividade();
    }

    public void setRestaurante(Restaurante rest) {
        this.restaurante = rest;
        carregarCardapio();

        this.atualizador = new AtualizadorAutomatico(5, this::carregarCardapio);
        this.atualizador.iniciar();
    }

    public void setClienteLogado(Cliente cliente) {
        clienteLogado = cliente;
    }

    public static Cliente getClienteLogado() {
        return clienteLogado;
    }

    public static List<ItemPedido> getCarrinho() {
        return carrinho;
    }

    public static void limparCarrinho() {
        carrinho.clear();
    }

    private void configurarResponsividade() {
        entradaGrid.widthProperty().addListener((obs, antigo, novo) -> redistribuirCardapio());
        principalGrid.widthProperty().addListener((obs, antigo, novo) -> redistribuirCardapio());
        sobremesaGrid.widthProperty().addListener((obs, antigo, novo) -> redistribuirCardapio());
        bebidasGrid.widthProperty().addListener((obs, antigo, novo) -> redistribuirCardapio());
    }

    private void carregarCardapio() {
        CardapioRepository cardapioRepository = new CardapioRepository();
        itensDisponiveis = cardapioRepository.buscarSomenteDisponiveis();
        redistribuirCardapio();
    }

    private void redistribuirCardapio() {
        entradaGrid.getChildren().clear();
        principalGrid.getChildren().clear();
        sobremesaGrid.getChildren().clear();
        bebidasGrid.getChildren().clear();

        if (itensDisponiveis == null || itensDisponiveis.isEmpty()) {
            return;
        }

        int idxEntrada = 0;
        int idxPrincipal = 0;
        int idxSobremesa = 0;
        int idxBebidas = 0;

        for (ItemCardapio item : itensDisponiveis) {
            VBox card = criarCard(item);

            switch (item.getCategoria()) {
                case ENTRADA -> adicionarCard(entradaGrid, card, idxEntrada++);
                case PRATO_PRINCIPAL -> adicionarCard(principalGrid, card, idxPrincipal++);
                case SOBREMESA -> adicionarCard(sobremesaGrid, card, idxSobremesa++);
                case BEBIDAS -> adicionarCard(bebidasGrid, card, idxBebidas++);
            }
        }
    }

    private void adicionarCard(GridPane grid, VBox card, int indice) {
        int colunas = calcularColunas(grid);
        grid.add(card, indice % colunas, indice / colunas);
    }

    private int calcularColunas(GridPane grid) {
        double largura = grid.getWidth();
        if (largura <= 0) {
            largura = 900;
        }
        return Math.max(1, (int) ((largura + CARD_GAP) / (CARD_WIDTH + CARD_GAP)));
    }

    private VBox criarCard(ItemCardapio item) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: white; " +
                "-fx-padding: 12; " +
                "-fx-border-color: #d1d5db; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 4, 0, 0, 1);"
        );
        card.setPrefWidth(CARD_WIDTH);
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);
        card.setPrefHeight(330);
        card.setMinHeight(330);
        card.setAlignment(Pos.TOP_LEFT);

        ImageView imagem = new ImageView(ImagemUtil.carregar(item.getImagemPath()));
        imagem.setFitWidth(180);
        imagem.setFitHeight(100);
        imagem.setPreserveRatio(true);
        imagem.setStyle("-fx-border-radius: 4;");

        Label nome = new Label(item.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #064E3B;");
        nome.setWrapText(true);
        nome.setMaxWidth(180);

        Label descricao = new Label(item.getDescricao() != null ? item.getDescricao() : "");
        descricao.setStyle("-fx-font-size: 12px; -fx-text-fill: #4B5563;");
        descricao.setWrapText(true);
        descricao.setMaxWidth(180);
        descricao.setMaxHeight(72);
        if (descricao.getText().isBlank()) {
            descricao.setVisible(false);
            descricao.setManaged(false);
        }

        Label preco = new Label(String.format("R$ %.2f", item.getPreco()));
        preco.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #962828;");

        Label status = new Label(item.isDisponivel() ? "Disponivel" : "Indisponivel");
        status.setStyle("-fx-font-size: 11px; -fx-text-fill: " +
                (item.isDisponivel() ? "#16a34a;" : "#dc2626;") +
                " -fx-font-weight: bold;");

        HBox acoes = new HBox(8);
        acoes.setAlignment(Pos.CENTER_LEFT);

        Spinner<Integer> quantidade = new Spinner<>(1, 99, 1);
        quantidade.setPrefWidth(60);

        Button adicionarButton = new Button("Adicionar");
        adicionarButton.setStyle(
                "-fx-background-color: #34D399; " +
                "-fx-text-fill: #064E3B; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5;"
        );
        adicionarButton.setCursor(javafx.scene.Cursor.HAND);
        adicionarButton.setOnAction(e -> {
            carrinho.add(new ItemPedido(item, quantidade.getValue()));
            carrinhoButton.setText("Carrinho (" + carrinho.size() + ")");
        });

        acoes.getChildren().addAll(quantidade, adicionarButton);
        card.getChildren().addAll(imagem, nome, descricao, preco, status, acoes);

        VBox.setMargin(imagem, new Insets(0, 0, 5, 0));
        VBox.setMargin(acoes, new Insets(5, 0, 0, 0));

        return card;
    }

    @FXML
    void abrirCarrinho(ActionEvent event) throws IOException {
        if (atualizador != null) {
            atualizador.parar();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carrinho.fxml"));
        Parent root = loader.load();

        CarrinhoController controller = loader.getController();
        controller.setCliente(clienteLogado);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Carrinho");
        stage.show();
    }

    @FXML
    void abrirHistorico(ActionEvent event) throws IOException {
        if (atualizador != null) {
            atualizador.parar();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historico-pedidos.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Historico");
        stage.show();
    }

    @FXML
    void sair(ActionEvent event) throws IOException {
        limparCarrinho();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Login");
        stage.show();
    }
}
