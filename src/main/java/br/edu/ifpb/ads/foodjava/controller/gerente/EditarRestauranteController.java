package br.edu.ifpb.ads.foodjava.controller.gerente;

import br.edu.ifpb.ads.foodjava.controller.autenticacao.LoginController;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class EditarRestauranteController {

    @FXML private TextField nomeFantasiaField;
    @FXML private TextField cnpjField;
    @FXML private TextField telefoneField;
    @FXML private Label telefoneAvisoLabel;
    @FXML private TextField localizacaoField;
    @FXML private ComboBox<CategoriaCulinaria> categoriaComboBox;
    @FXML private ImageView logoPreview;
    @FXML private Button salvarButton;
    @FXML private Button editarLogoButton;

    private final RestauranteRepository repository = new RestauranteRepository();
    private Restaurante restaurante;
    private String logoPath;

    @FXML
    void initialize() {
        categoriaComboBox.getItems().setAll(CategoriaCulinaria.values());
        telefoneAvisoLabel.setVisible(false);
        carregarRestaurante();
    }

    private void carregarRestaurante() {
        restaurante = LoginController.getRestaurante();
        if (restaurante == null) {
            restaurante = repository.buscar();
        }

        if (restaurante == null) {
            mostrarAlerta("Sem restaurante", "Nenhum restaurante foi encontrado para edicao.");
            return;
        }

        nomeFantasiaField.setText(restaurante.getNomeFantasia());
        cnpjField.setText(restaurante.getCnpj());
        telefoneField.setText(restaurante.getTelefone());
        localizacaoField.setText(restaurante.getEndereco());
        categoriaComboBox.setValue(restaurante.getCategoriaCulinaria());

        logoPath = restaurante.getLogotipoPath();
        ImagemUtil.carregarLogoDoJson(logoPreview, RestauranteRepository.getCaminhoArquivo());
        if (logoPath != null && !logoPath.isBlank()) {
            logoPreview.setImage(ImagemUtil.carregar(logoPath));
        }
    }

    @FXML
    void editarLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar nova logo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File arquivo = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (arquivo != null) {
            logoPath = ImagemUtil.salvar(arquivo);
            logoPreview.setImage(ImagemUtil.carregar(logoPath));
        }
    }

    @FXML
    void salvarEdicao(ActionEvent event) {
        if (!camposValidos()) {
            return;
        }

        restaurante.setNomeFantasia(nomeFantasiaField.getText().trim());
        restaurante.setCnpj(cnpjField.getText().trim());
        restaurante.setTelefone(telefoneField.getText().trim());
        restaurante.setEndereco(localizacaoField.getText().trim());
        restaurante.setCategoriaCulinaria(categoriaComboBox.getValue());
        restaurante.setLogotipoPath(logoPath);

        repository.salvar(restaurante);
        LoginController.setRestaurante(restaurante);

        mostrarAlerta("Sucesso", "Dados do restaurante atualizados com sucesso.");
        voltarPainel(event);
    }

    @FXML
    void voltarPainel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/painel-gerente.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) salvarButton.getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Erro", "Nao foi possivel voltar ao painel do gerente.");
        }
    }

    private boolean camposValidos() {
        if (campoVazio(nomeFantasiaField.getText())
                || campoVazio(cnpjField.getText())
                || campoVazio(telefoneField.getText())
                || campoVazio(localizacaoField.getText())
                || categoriaComboBox.getValue() == null) {
            mostrarAlerta("Validacao", "Preencha todos os campos obrigatorios.");
            return false;
        }

        telefoneAvisoLabel.setVisible(false);
        if (!telefoneValido(telefoneField.getText())) {
            telefoneAvisoLabel.setText("Informe pelo menos 10 digitos.");
            telefoneAvisoLabel.setVisible(true);
            return false;
        }

        return true;
    }

    private boolean campoVazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean telefoneValido(String telefone) {
        String somenteDigitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        return somenteDigitos.length() >= 10;
    }
}
