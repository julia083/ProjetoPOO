package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import br.edu.ifpb.ads.foodjava.util.DocumentoUtil;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class ConfiguracaoInicialController {

    @FXML private Button logoObrigatoria;
    @FXML private Button configurarRestauranteBotton;
    @FXML private TextField nomeFantasiaField;
    @FXML private TextField cnpjField;
    @FXML private TextField telefoneField;
    @FXML private Label telefoneAvisoLabel;
    @FXML private TextField localizacaoField;
    @FXML private ComboBox<CategoriaCulinaria> categoriaComboBox;

    private String logoPath;

    @FXML
    public void initialize() {
        categoriaComboBox.getItems().setAll(CategoriaCulinaria.values());
        telefoneAvisoLabel.setVisible(false);
    }

    @FXML
    void selecioneLogotipo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Logotipo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo != null) {
            this.logoPath = ImagemUtil.salvar(arquivo);
        }
    }

    @FXML
    void configurarORestaurante(ActionEvent event) {
        String nomeFantasia = nomeFantasiaField.getText();
        String cnpj = cnpjField.getText();
        String telefone = telefoneField.getText();
        String localizacao = localizacaoField.getText();
        CategoriaCulinaria categoria = categoriaComboBox.getValue();

        telefoneAvisoLabel.setVisible(false);

        if (campoVazio(nomeFantasia)) {
            mostrarAlerta("Erro de Validacao", "O campo Nome Fantasia e obrigatorio.");
            return;
        }
        if (campoVazio(cnpj)) {
            mostrarAlerta("Erro de Validacao", "O campo CNPJ e obrigatorio.");
            return;
        }
        if (campoVazio(telefone)) {
            mostrarAlerta("Erro de Validacao", "O campo Telefone e obrigatorio.");
            return;
        }
        if (!telefoneValido(telefone)) {
            telefoneAvisoLabel.setText("Informe pelo menos 10 digitos.");
            telefoneAvisoLabel.setVisible(true);
            return;
        }
        if (campoVazio(localizacao)) {
            mostrarAlerta("Erro de Validacao", "O campo Localizacao completa e obrigatorio.");
            return;
        }
        if (categoria == null) {
            mostrarAlerta("Erro de Validacao", "O campo Categoria e obrigatorio.");
            return;
        }

        try {
            DocumentoUtil.validarCnpj(cnpj);
        } catch (DocumentoInvalidoException e) {
            mostrarAlerta("CNPJ Invalido", e.getMessage());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configuracao-restaurante.fxml"));
            Parent root = loader.load();

            ConfiguracaoRestauranteController proximoController = loader.getController();
            proximoController.setDadosRestaurante(
                    nomeFantasia.trim(),
                    cnpj.trim(),
                    telefone.trim(),
                    localizacao.trim(),
                    categoria,
                    this.logoPath
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir a tela de cadastro do gerente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean campoVazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean telefoneValido(String telefone) {
        String somenteDigitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        return somenteDigitos.length() >= 10;
    }
}
