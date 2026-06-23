package br.edu.ifpb.ads.foodjava;

import br.edu.ifpb.ads.foodjava.controller.autenticacao.LoginController;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Carrega o restaurante do JSON
        RestauranteRepository repo = new RestauranteRepository();
        Restaurante restaurante = repo.buscar();

        String fxmlParaCarregar;

        if (restaurante != null) {
            // Restaurante já existe → abre login
            fxmlParaCarregar = "/fxml/login.fxml";
            // Opcional: já deixa o restaurante disponível no LoginController
            LoginController.setRestaurante(restaurante);
        } else {
            // Primeira execução → abre configuração inicial
            fxmlParaCarregar = "/fxml/configuracao-inicial.fxml";
        }

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource(fxmlParaCarregar)
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("FoodJava");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}