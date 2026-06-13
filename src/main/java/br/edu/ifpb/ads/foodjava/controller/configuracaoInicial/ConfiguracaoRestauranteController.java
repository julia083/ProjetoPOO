package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class ConfiguracaoRestauranteController {

    @FXML
    private ComboBox<CategoriaCulinaria> cbCategoria;

    @FXML
    private TextField cnpjRestaurante;

    @FXML
    private TextField cpfGerente;

    @FXML
    private TextField emailGerente;

    @FXML
    private TextField enderecoRestaurante;

    @FXML
    private TextField nomeFantasia;

    @FXML
    private TextField nomeGerente;

    @FXML
    private Button salvarRestaurante;

    @FXML
    private Button selecionarLogoTipo;

    @FXML
    private PasswordField senhaGerente;

    @FXML
    private TextField telefoneRestaurante;

    @FXML
    public void initialize() {
        cbCategoria.getItems().addAll(CategoriaCulinaria.values());
    }

    private File logoSelecionada;

    void salvarORestaurante(ActionEvent event) {

        Gerente gerente = new Gerente(nomeGerente.getText(),
                cpfGerente.getText(),
                emailGerente.getText(),
                senhaGerente.getText()
        );

        Restaurante restaurante = new Restaurante(
                nomeFantasia.getText(),
                cnpjRestaurante.getText(),
                enderecoRestaurante.getText(),
                telefoneRestaurante.getText(),
                cbCategoria.getValue(),
                logoSelecionada != null
                        ? logoSelecionada.getAbsolutePath()
                        : null, gerente);

        if (!restaurante.validar()) {
            System.out.println("Dados inválidos.");
            return;
        }

        System.out.println("Restaurante cadastrado com sucesso!");
    }
}