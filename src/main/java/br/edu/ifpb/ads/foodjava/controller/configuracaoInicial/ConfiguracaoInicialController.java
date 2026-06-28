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
            this.logoPath = ImagemUtil.salvar(arquivo);
        }
    }

    @FXML
    void configurarORestaurante(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configuracao-restaurante.fxml"));
            Parent root = loader.load();

            // Resgata o controller real criado pelo JavaFX
            ConfiguracaoRestauranteController proximoController = loader.getController();

            // Passa o logoPath (que pode ser o caminho do arquivo ou null)
            proximoController.setLogo(this.logoPath);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir a tela de configurações: " + e.getMessage());
            e.printStackTrace();
        }
    }
}