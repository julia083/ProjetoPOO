package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class ConfiguracaoRestauranteController {
    @FXML
    private TextField bairroRestaurante;

    @FXML
    private TextField categoriaCulinaria;

    @FXML
    private TextField cnpjRestaurante;

    @FXML
    private TextField cpfGerente;

    @FXML
    private TextField emailGerente;

    @FXML
    private TextField nomeFantasia;

    @FXML
    private TextField nomeGerente;

    @FXML
    private TextField numeroRestaurante;

    @FXML
    private TextField ruaRestaurante;

    @FXML
    private Button salvarRestauranteBotton;

    @FXML
    private PasswordField senhaGerente;

    @FXML
    private TextField telefoneRestaurante;

    @FXML
    void salvarORestaurante(ActionEvent event) {

    }
    @FXML
    void selecionarLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Selecionar Logotipo");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            System.out.println(arquivo.getAbsolutePath());
        }
    }
}