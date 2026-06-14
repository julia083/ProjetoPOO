package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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

    @FXML
    private void selecionarLogo(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Selecionar Logotipo");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imagens", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            logoSelecionada = arquivo;
            System.out.println("Logo selecionada: " + arquivo.getAbsolutePath());
        }
    }

    private File logoSelecionada;

    @FXML
    void salvarORestaurante(ActionEvent event) throws IOException{

        Gerente gerente = new Gerente(
                nomeGerente.getText(),
                emailGerente.getText(),
                senhaGerente.getText(),
                cpfGerente.getText()
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

        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
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