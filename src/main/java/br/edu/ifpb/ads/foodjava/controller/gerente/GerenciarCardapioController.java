package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciarCardapioController {

    @FXML
    private ComboBox<Categoria> categoriaDoItem;

    @FXML
    private TableColumn<ItemCardapio, String> colCategoria;

    @FXML
    private TableColumn<ItemCardapio, String> colDescricao;

    @FXML
    private TableColumn<ItemCardapio, String> colNome;

    @FXML
    private TableColumn<ItemCardapio, String> colPreco;

    @FXML
    private TextArea descricaoArea;

    @FXML
    private Button editarButton;

    @FXML
    private Button excluirButton;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField precoField;

    @FXML
    private Button salvarButton;

    @FXML
    private TableView<ItemCardapio> tabelaItens;

    @FXML
    private Button voltarButton;

    @FXML
    void initialize() {
        configurarCampos();
        configurarTabela();
    }

    private void configurarCampos() {
        categoriaDoItem.getItems().setAll(Categoria.values());
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNome()));
        colDescricao.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDescricao()));
        colPreco.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.format("R$ %.2f", cellData.getValue().getPreco())));
        colCategoria.setCellValueFactory(cellData -> {
            Categoria categoria = cellData.getValue().getCategoria();
            return new ReadOnlyStringWrapper(categoria == null ? "" : categoria.name());
        });
    }

    @FXML
    void editarItem(ActionEvent event) {

    }

    @FXML
    void excluirItem(ActionEvent event) {

    }

    @FXML
    void salvarItem(ActionEvent event) {

    }

    @FXML
    void voltarPainelGerente(ActionEvent event) throws IOException{
        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/painel-gerente.fxml"));
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

}
