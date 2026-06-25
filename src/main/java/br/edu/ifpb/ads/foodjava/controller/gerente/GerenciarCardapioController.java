package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.exception.ArquivoImportacaoException;
import br.edu.ifpb.ads.foodjava.exception.PrecoInvalidoException;
import br.edu.ifpb.ads.foodjava.model.ItemCardapio;
import br.edu.ifpb.ads.foodjava.model.enums.Categoria;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
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
import br.edu.ifpb.ads.foodjava.interfaces.Validavel;

import br.edu.ifpb.ads.foodjava.repository.CardapioRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.exibirAlerta;
import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class GerenciarCardapioController implements Validavel {

    @FXML
    private Button alterarDisponibilidadeButton;

    @FXML
    private ComboBox<Categoria> categoriaDoItem;

    @FXML
    private TableColumn<ItemCardapio, String> colCategoria;
    @FXML
    private TableView<ItemCardapio> tabelaItens;

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
    private Button voltarButton;

    @FXML
    void initialize() {
        configurarCampos();
        configurarTabela();
        atualizarTabelaCardapio();
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
            mostrarAlerta("Aviso", "Selecione um item.");
            return;
        }

        if (itemSelecionado.isDisponivel()) {
            itemSelecionado.desativar();
        } else {
            itemSelecionado.ativar();
        }

        repository.atualizar(itemSelecionado);

        atualizarTabelaCardapio();
    }

    private ItemCardapio itemEditado = null;

    @FXML
    void editarItem(ActionEvent event) {
        // 1. Capta o item selecionado diretamente da TableView [1]
        itemEditado = tabelaItens.getSelectionModel().getSelectedItem();

        if (itemEditado != null) {
            // 2. Dispõe as informações nos campos para que possam ser editados [2, 3]
            nomeField.setText(itemEditado.getNome().trim());
            descricaoArea.setText(itemEditado.getDescricao());
            precoField.setText(String.valueOf(itemEditado.getPreco()));
            categoriaDoItem.setValue(itemEditado.getCategoria());
            disponivelCheckBox.setSelected(itemEditado.isDisponivel());

            // Carrega a imagem no preview se houver um caminho salvo [1]
            if (itemEditado.getImagemPath() != null) {
                imagemPreview.setImage(ImagemUtil.carregar(itemEditado.getImagemPath()));
            }

            // 3. Muda temporariamente o nome do botão para "Salvar Alterações"
            salvarButton.setText("Salvar Alterações");
        } else {
            mostrarAlerta("Aviso", "Selecione um item na tabela antes de clicar em editar.");
        }

    }

    @FXML
    void excluirItem(ActionEvent event) {
        itemSelecionado = tabelaItens.getSelectionModel().getSelectedItem();
        repository.deletar(itemSelecionado.getNome());
        atualizarTabelaCardapio();
    }

    CardapioRepository repository = new CardapioRepository();

    @FXML
    void importarJson(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Importar Cardapio JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos JSON", "*.json"));

        File arquivo = fc.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (arquivo != null) {
            try {
                // O repositório processa e devolve a lista de erros parciais
                List<String> erros = repository.importarCardapio(arquivo.getAbsolutePath());

                if (erros.isEmpty()) {
                    exibirAlerta("Sucesso", "Importação Concluída", "Todos os itens foram importados.", Alert.AlertType.INFORMATION);
                } else {
                    // REQUISITO: Exibir relatório linha a linha [1]
                    String relatorio = String.join("\n", erros);
                    exibirAlerta("Importação Parcial", "Alguns itens foram ignorados:", relatorio, Alert.AlertType.WARNING);
                }

                atualizarTabelaCardapio();

            } catch (ArquivoImportacaoException e) {
                // REQUISITO: Tratar erro crítico de arquivo ausente/vazio [7]
                exibirAlerta("Erro Crítico", "Falha no arquivo", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void salvarItem(ActionEvent event) {

        if(!validar()){
            return;
        }

        String nome = nomeField.getText().trim();
        String descricao = descricaoArea.getText();
        double preco = Double.parseDouble(precoField.getText());
        Categoria categoria = categoriaDoItem.getValue();
        boolean disponivel = disponivelCheckBox.isSelected();

        try {
            ItemCardapio novoItem = new ItemCardapio(nome, descricao, preco, categoria, disponivel, pathImagem);

            if(itemEditado != null){
                repository.atualizar(novoItem);
                atualizarTabelaCardapio();
                limparCampos();
                return;
            }

            repository.salvar(novoItem);
            atualizarTabelaCardapio();
            limparCampos();

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
    private void atualizarTabelaCardapio() {
        // Para atualizar o JavaFX, usamos ObservableList
        List<ItemCardapio> lista = repository.listarTodos();
        tabelaItens.setItems(FXCollections.observableArrayList(lista));
        tabelaItens.refresh();
    }

    //valida o item antes de armazenar
    public boolean validar(){

        if (nomeField.getText().trim().isEmpty() ||
                precoField.getText().trim().isEmpty() ||
                categoriaDoItem.getValue() == null) {

            mostrarAlerta("Erro", "Nome, preço e categoria devem estar devidamente preenchidos!");
            return false;
        }

        return true;
    }

    private void limparCampos(){
        nomeField.clear();
        precoField.clear();
        descricaoArea.clear();
        categoriaDoItem.setValue(null);
        imagemPreview.setImage(null);
        salvarButton.setText("Salvar");
    }
}