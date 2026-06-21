package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import br.edu.ifpb.ads.foodjava.repository.CardapioRepository;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.scene.image.ImageView;
public class CardapioController {

    @FXML private Button carrinhoButton;
    @FXML private GridPane entradaGrid, principalGrid, sobremesaGrid, bebidasGrid;

    private Restaurante restaurante;
    private static Cliente clienteLogado;
    private static List<ItemPedido> carrinho = new ArrayList<>();


    public void setRestaurante(Restaurante rest) {
        this.restaurante = rest;
        carregarCardapio();
    }



    public static List<ItemPedido> getCarrinho() {
        return carrinho;
    }


    public void setClienteLogado(Cliente cliente) {
        clienteLogado = cliente;
    }

    public static Cliente getClienteLogado() {
        return clienteLogado;
    }

    public static void limparCarrinho() {
        carrinho.clear();
    }

    private void carregarCardapio() {
        CardapioRepository cardapioRepository = new CardapioRepository();
        List<ItemCardapio> itens = cardapioRepository.buscarSomenteDisponiveis();

        for (ItemCardapio item : itens) {
            VBox card = criarCard(item);
            switch (item.getCategoria()) {
                case ENTRADA -> entradaGrid.add(card, 0, entradaGrid.getChildren().size());
                case PRATO_PRINCIPAL -> principalGrid.add(card, 0, principalGrid.getChildren().size());
                case SOBREMESA -> sobremesaGrid.add(card, 0, sobremesaGrid.getChildren().size());
                case BEBIDAS -> bebidasGrid.add(card, 0, bebidasGrid.getChildren().size());
            }
        }
    }

    private VBox criarCard(ItemCardapio item) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #34D399;");

        ImageView imagem = new ImageView(ImagemUtil.carregar(item.getImagemPath()));
        imagem.setFitWidth(150);
        imagem.setFitHeight(100);
        imagem.setPreserveRatio(true);

        Label nome = new Label(item.getNome());
        nome.setStyle("-fx-font-weight: bold;");

        Label preco = new Label(String.format("R$ %.2f", item.getPreco()));

        Spinner<Integer> qtd = new Spinner<>(1, 99, 1);
        Button add = new Button("Adicionar");
        add.setOnAction(e -> {
            carrinho.add(new ItemPedido(item, qtd.getValue()));
            carrinhoButton.setText("🛒 (" + carrinho.size() + ")");
        });

        card.getChildren().addAll(imagem, nome, preco, qtd, add);
        return card;
    }

    @FXML
    void abrirCarrinho(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carrinho.fxml"));
        Parent root = loader.load();

        CarrinhoController controller = loader.getController();
        controller.setCliente(clienteLogado);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Carrinho");
    }


    @FXML
    void abrirHistorico(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historico-pedidos.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Histórico");
    }

    @FXML
    void sair(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FoodJava - Login");
    }


}
