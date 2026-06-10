package br.edu.ifpb.ads.foodjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/configuracao-inicial.fxml")
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