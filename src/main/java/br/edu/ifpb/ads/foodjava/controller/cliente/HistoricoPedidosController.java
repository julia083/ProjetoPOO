package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HistoricoPedidosController {

    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private ListView<String> listaPedidos;

    @FXML
    public void initialize() {
        carregarPedidos();
    }

    private void carregarPedidos() {
        listaPedidos.getItems().clear();

        String email = CardapioController.getEmailClienteLogado();
        if (email == null) {
            listaPedidos.getItems().add("Cliente não identificado.");
            return;
        }

        // Busca os pedidos do cliente (simulação - substituir pelo Repository real)
        List<Pedido> pedidos = PedidoRepository.buscarPorCliente(email);

        if (pedidos.isEmpty()) {
            listaPedidos.getItems().add("Nenhum pedido encontrado.");
            return;
        }

        // Ordena por data (mais recente primeiro)
        pedidos.sort((p1, p2) -> p2.getDataHora().compareTo(p1.getDataHora()));

        for (Pedido p : pedidos) {
            String id = p.getId().substring(0, Math.min(8, p.getId().length()));
            String data = p.getDataHora().format(FORMATO_DATA);
            String total = FORMATO_MOEDA.format(p.getValorTotal());
            String status = p.getStatus().toString().replace("_", " ");

            String linha = String.format("#%s | %s | %s | %s", id, data, total, status);
            listaPedidos.getItems().add(linha);
        }
    }

    @FXML
    void voltarCardapio(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/cardapio.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FoodJava - Cardápio");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível voltar ao cardápio.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

