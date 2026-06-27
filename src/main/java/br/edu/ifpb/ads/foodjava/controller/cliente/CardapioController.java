package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.repository.CardapioRepository;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardapioController {

    @FXML private Button carrinhoButton;
    @FXML private GridPane entradaGrid, principalGrid, sobremesaGrid, bebidasGrid;

    private Restaurante restaurante;
    private static Cliente clienteLogado;
    private static List<ItemPedido> carrinho = new ArrayList<>();
    private AtualizadorAutomatico atualizador;

    // ===== SETTERS E GETTERS =====

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

    // ===== CARREGAR CARDÁPIO =====

    private void carregarCardapio() {
        CardapioRepository cardapioRepository = new CardapioRepository();
        List<ItemCardapio> itens = cardapioRepository.buscarSomenteDisponiveis();

        // Limpa as grids antes de adicionar
        entradaGrid.getChildren().clear();
        principalGrid.getChildren().clear();
        sobremesaGrid.getChildren().clear();
        bebidasGrid.getChildren().clear();

        // Número de colunas por linha
        int colunas = 3;

        // Índices para controlar posição em cada grid
        int idxEntrada = 0, idxPrincipal = 0, idxSobremesa = 0, idxBebidas = 0;

        for (ItemCardapio item : itens) {
            VBox card = criarCard(item);
            int col, row;

            switch (item.getCategoria()) {
                case ENTRADA:
                    col = idxEntrada % colunas;
                    row = idxEntrada / colunas;
                    entradaGrid.add(card, col, row);
                    idxEntrada++;
                    break;
                case PRATO_PRINCIPAL:
                    col = idxPrincipal % colunas;
                    row = idxPrincipal / colunas;
                    principalGrid.add(card, col, row);
                    idxPrincipal++;
                    break;
                case SOBREMESA:
                    col = idxSobremesa % colunas;
                    row = idxSobremesa / colunas;
                    sobremesaGrid.add(card, col, row);
                    idxSobremesa++;
                    break;
                case BEBIDAS:
                    col = idxBebidas % colunas;
                    row = idxBebidas / colunas;
                    bebidasGrid.add(card, col, row);
                    idxBebidas++;
                    break;
            }
        }
    }

    // ===== CRIAÇÃO DO CARD MELHORADO =====

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
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setAlignment(Pos.TOP_LEFT);

        // --- IMAGEM (ao topo, pequena) ---
        ImageView imagem = new ImageView(ImagemUtil.carregar(item.getImagemPath()));
        imagem.setFitWidth(180);
        imagem.setFitHeight(100);
        imagem.setPreserveRatio(true);
        imagem.setStyle("-fx-border-radius: 4;");
        // Se não houver imagem, usa placeholder


        // --- NOME ---
        Label nome = new Label(item.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #064E3B;");
        nome.setWrapText(true);

        // --- DESCRIÇÃO (se houver) ---
        Label descricao = new Label(item.getDescricao() != null ? item.getDescricao() : "");
        descricao.setStyle("-fx-font-size: 12px; -fx-text-fill: #4B5563;");
        descricao.setWrapText(true);
        descricao.setMaxWidth(180);
        if (descricao.getText().isBlank()) {
            descricao.setVisible(false);
        }

        // --- PREÇO ---
        Label preco = new Label(String.format("R$ %.2f", item.getPreco()));
        preco.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #962828;");

        // --- STATUS (disponível / indisponível) ---
        Label status = new Label("🟢 Disponível");
        status.setStyle("-fx-font-size: 11px; -fx-text-fill: #16a34a; -fx-font-weight: bold;");
        if (!item.isDisponivel()) {
            status.setText("🔴 Indisponível");
            status.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        }

        // --- QUANTIDADE E BOTÃO ADICIONAR (em linha) ---
        HBox acoes = new HBox(8);
        acoes.setAlignment(Pos.CENTER_LEFT);
        Spinner<Integer> qtd = new Spinner<>(1, 99, 1);
        qtd.setPrefWidth(60);
        Button add = new Button("Adicionar");
        add.setStyle(
                "-fx-background-color: #34D399; " +
                        "-fx-text-fill: #064E3B; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"
        );
        add.setCursor(javafx.scene.Cursor.HAND);
        add.setOnAction(e -> {
            carrinho.add(new ItemPedido(item, qtd.getValue()));
            carrinhoButton.setText("🛒 (" + carrinho.size() + ")");
        });
        acoes.getChildren().addAll(qtd, add);

        // --- MONTAGEM DO CARD ---
        card.getChildren().addAll(imagem, nome, descricao, preco, status, acoes);

        // Espaçamento entre seções
        VBox.setMargin(imagem, new Insets(0, 0, 5, 0));
        VBox.setMargin(acoes, new Insets(5, 0, 0, 0));

        return card;
    }

    // ===== AÇÕES DOS BOTÕES =====

    @FXML
    void abrirCarrinho(ActionEvent event) throws IOException {
        if (atualizador!=null){
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
        if (atualizador!=null){
            atualizador.parar();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historico-pedidos.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Histórico");
        stage.show();
    }

    @FXML
    void sair(ActionEvent event) throws IOException {
        if (atualizador!=null){
            atualizador.parar();
        }
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Login");
        stage.show();
    }
}

