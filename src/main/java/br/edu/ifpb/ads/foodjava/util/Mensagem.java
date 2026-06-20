package br.edu.ifpb.ads.foodjava.util;

import javafx.scene.control.Alert;

public class Mensagem {

    public static void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}