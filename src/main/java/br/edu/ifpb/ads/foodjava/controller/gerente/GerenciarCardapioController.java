package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.exception.PrecoInvalidoException;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;

import br.edu.ifpb.ads.foodjava.repository.CardapioRepository;

import java.io.File;
import java.io.IOException;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

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

    private ItemCardapio itemSelecionado = null;

    @FXML
    void alterarDisponibilidade(ActionEvent event) {
        itemSelecionado = tabelaItens.getSelectionModel().getSelectedItem();

        if (itemSelecionado == null) {
            return;
        }

        if (itemSelecionado.isDisponivel()) {
            itemSelecionado.desativar();
        } else {
            itemSelecionado.ativar();
        }

        tabelaItens.refresh();
    }

    @FXML
    void editarItem(ActionEvent event) {

    }

    @FXML
    void excluirItem(ActionEvent event) {
        // Lógica para deletar o item selecionado
    }

    @FXML
    void importarJson(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Cardápio (JSON)");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Arquivos JSON (*.json)", "*.json"
                )
        );

        File arquivoSelecionado = fileChooser.showOpenDialog(null);

        if (arquivoSelecionado != null) {

            String caminhoJson = arquivoSelecionado.getAbsolutePath();

            CardapioRepository repository = new CardapioRepository();
            repository.importarCardapio(caminhoJson);

            System.out.println("Arquivo selecionado para importação: " + caminhoJson);
        } else {
            System.out.println("Nenhum arquivo foi selecionado.");
        }

    }

    @FXML
    void salvarItem(ActionEvent event) {
        try {
            String nome = nomeField.getText();
            String descricao = descricaoArea.getText();
            double preco = Double.parseDouble(precoField.getText());
            Categoria categoria = categoriaDoItem.getValue();
            boolean disponivel = disponivelCheckBox.isSelected();

            ItemCardapio novoItem = new ItemCardapio(nome, descricao, preco, categoria, disponivel, pathImagem);

            if (!novoItem.validar()) {
                mostrarAlerta("Item inválido", "Nome, preço ou categoria inválidos!");
                return;
            }

            CardapioRepository repositorio = new CardapioRepository();
            repositorio.salvar(novoItem);

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato de número inválido", e.getMessage());
        } catch (PrecoInvalidoException e) {
            mostrarAlerta("Preço inválido", e.getMessage());
        }
    }


    private String pathImagem;

    @FXML
    void selecionarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Selecionar Imagem");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            pathImagem = ImagemUtil.salvar(arquivo);
            imagemPreview.setImage(ImagemUtil.carregar(pathImagem));
        }
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