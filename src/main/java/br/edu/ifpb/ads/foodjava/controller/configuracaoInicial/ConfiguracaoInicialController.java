package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfiguracaoInicialController {

    @FXML
    private Button configurarRestauranteBotton;

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
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}