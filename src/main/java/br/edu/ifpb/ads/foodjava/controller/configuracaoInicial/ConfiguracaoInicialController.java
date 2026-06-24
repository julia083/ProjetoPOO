package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.exibirAlerta;
import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class ConfiguracaoInicialController {

    @FXML
    private Button logoObrigatoria;

    @FXML
    private Button configurarRestauranteBotton;

    private String logoPath;

    @FXML
    void selecioneLogotipo(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Logotipo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            logoPath = ImagemUtil.salvar(arquivo);
        }
        ConfiguracaoRestauranteController repo = new ConfiguracaoRestauranteController();
        repo.setLogo(logoPath);
    }

    @FXML
    void configurarORestaurante(ActionEvent event) throws IOException {

        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configuracao-restaurante.fxml"));
            Parent root = loader.load();

            // 2. Pega a janela (Stage) atual a partir do botão que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Define a nova cena na mesma janela
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Erro", e.getMessage());
            e.printStackTrace();
        }
    }
}