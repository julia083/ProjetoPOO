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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciarCardapioController {

    @FXML
    private Button alterarDisponibilidadeButton;

    @FXML
    private ComboBox<Categoria> categoriaDoItem;

    @FXML
    private TableColumn<ItemCardapio, String> colCategoria;

    @FXML
    private TableColumn<ItemCardapio, String> colDescricao;

    @FXML
    private TableColumn<ItemCardapio, String> colDisponibilidade;

    @FXML
    private TableColumn<ItemCardapio, String> colNome;

    @FXML
    private TableColumn<ItemCardapio, String> colPreco;

    @FXML
    private TextArea descricaoArea;

    @FXML
    private CheckBox disponivelCheckBox;

    @FXML
    private Button editarButton;

    @FXML
    private Button excluirButton;

    @FXML
    private ImageView imagemPreview;

    @FXML
    private Button importarJsonButton;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField precoField;

    @FXML
    private Button salvarButton;

    @FXML
    private Button selecionarImagemButton;

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

        // Configuração básica da nova coluna de Status/Disponibilidade
        colDisponibilidade.setCellValueFactory(cellData -> {
            boolean disponivel = cellData.getValue().isDisponivel(); // Certifique-se de que esse método existe na sua model
            return new ReadOnlyStringWrapper(disponivel ? "Ativo" : "Inativo");
        });
    }

    @FXML
    void alterarDisponibilidade(ActionEvent event) {
        // Lógica para alterar o status do item selecionado
    }

    @FXML
    void editarItem(ActionEvent event) {
        // Lógica para carregar os dados nos campos para edição
    }

    @FXML
    void excluirItem(ActionEvent event) {
        // Lógica para deletar o item selecionado
    }

    @FXML
    void importarJson(ActionEvent event) {
        // Lógica para carregar os dados via arquivo JSON
    }

    @FXML
    void salvarItem(ActionEvent event) {
        // Lógica para cadastrar ou atualizar o item
    }

    @FXML
    void selecionarImagem(ActionEvent event) {
        // Lógica usando FileChooser para escolher a imagem e atualizar o imagemPreview
    }

    @FXML
    void voltarPainelGerente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/painel-gerente.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}