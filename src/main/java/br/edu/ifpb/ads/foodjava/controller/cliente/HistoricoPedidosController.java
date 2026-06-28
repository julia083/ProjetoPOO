package br.edu.ifpb.ads.foodjava.controller.cliente;

import br.edu.ifpb.ads.foodjava.controller.autenticacao.LoginController;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.ItemPedido;
import br.edu.ifpb.ads.foodjava.model.Pedido;
import br.edu.ifpb.ads.foodjava.repository.PedidoRepository;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.AtualizadorAutomatico;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
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
    private AtualizadorAutomatico atualizador;

    @FXML
    private ListView<String> listaPedidos;

    @FXML private ImageView logoTipoView;

    @FXML
    private Label mensagemVazia;

    @FXML
    public void initialize() {
        carregarPedidos();
        ImagemUtil.carregarLogoDoJson(logoTipoView, RestauranteRepository.getCaminhoArquivo());
        atualizador = new AtualizadorAutomatico(1, this::carregarPedidos);
        atualizador.iniciar();

        // para o atualizador quando a tela for fechada ou trocada
        listaPedidos.sceneProperty().addListener((obs, cenaAntiga, cenaNova) -> {
            if (cenaNova == null) {
                atualizador.parar();
            }
        });
    }

    private void carregarPedidos() {
        listaPedidos.getItems().clear();
        mensagemVazia.setVisible(false);

        Cliente cliente = CardapioController.getClienteLogado();
        if (cliente == null || cliente.getId() == null) {
            mensagemVazia.setText("Cliente não identificado.");
            mensagemVazia.setVisible(true);
            return;
        }

        List<Pedido> pedidos = pedidoRepository.listarPorCliente(cliente.getId());

        if (pedidos.isEmpty()) {
            mensagemVazia.setText("Nenhum pedido encontrado.");
            mensagemVazia.setVisible(true);
            return;
        }

        pedidos.sort((p1, p2) -> {
            if (p1.getDataHora() == null) return 1;
            if (p2.getDataHora() == null) return -1;
            return p2.getDataHora().compareTo(p1.getDataHora());
        });

        for (Pedido p : pedidos) {
            listaPedidos.getItems().add(formatarPedido(p));
        }
    }

    private String formatarPedido(Pedido p) {
        StringBuilder sb = new StringBuilder();

        String id = p.getId() != null ? p.getId().substring(0, Math.min(8, p.getId().length())) : "??";
        String data = p.getDataHora() != null ? p.getDataHora().format(FORMATO_DATA) : "Data indisponível";
        String total = FORMATO_MOEDA.format(p.getValorTotal());
        String status = p.getStatus() != null ? p.getStatus().toString().replace("_", " ") : "Desconhecido";

        sb.append(String.format("#%s | %s | %s | %s", id, data, total, status));

        if (p.getItens() != null && !p.getItens().isEmpty()) {
            sb.append("\n  ");
            for (ItemPedido item : p.getItens()) {
                if (item.getItemCardapio() == null) continue;
                String nome = item.getItemCardapio().getNome();
                int qtd = item.getQuantidade();
                double subtotal = item.calcularSubtotal();
                sb.append(String.format("  %dx %s (%s)", qtd, nome, FORMATO_MOEDA.format(subtotal)));
            }
        } else {
            sb.append("\n  (Nenhum item)");
        }

        return sb.toString();
    }

    @FXML
    void voltarCardapio(ActionEvent event) {
        try {
            atualizador.parar();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardapio.fxml"));
            Parent root = loader.load();

            CardapioController controller = loader.getController();
            controller.setClienteLogado(CardapioController.getClienteLogado());
            controller.setRestaurante(LoginController.getRestaurante());

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