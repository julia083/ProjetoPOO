package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class HistoricoPedidosController {

    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidoRepository pedidoRepository = new PedidoRepository();

    @FXML
    private ListView<String> listaPedidos;

    @FXML
    public void initialize() {
        carregarPedidos();
    }

    private void carregarPedidos() {
        listaPedidos.getItems().clear();

        Cliente cliente = CardapioController.getClienteLogado();
        if (cliente == null || cliente.getId() == null) {
            listaPedidos.getItems().add("Cliente não identificado.");
            return;
        }

        List<Pedido> pedidos = pedidoRepository.listarPorCliente(cliente.getId());

        if (pedidos.isEmpty()) {
            listaPedidos.getItems().add("Nenhum pedido encontrado.");
            return;
        }

        // protege contra dataHora null
        pedidos.sort((p1, p2) -> {
            if (p1.getDataHora() == null) return 1;
            if (p2.getDataHora() == null) return -1;
            return p2.getDataHora().compareTo(p1.getDataHora());
        });

        for (Pedido p : pedidos) {
            String id = p.getId() != null
                    ? p.getId().substring(0, Math.min(8, p.getId().length()))
                    : "??";
            String data = p.getDataHora() != null
                    ? p.getDataHora().format(FORMATO_DATA)
                    : "Data indisponível";
            String total = FORMATO_MOEDA.format(p.getValorTotal());
            String status = p.getStatus() != null
                    ? p.getStatus().toString().replace("_", " ")
                    : "Desconhecido";

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

}