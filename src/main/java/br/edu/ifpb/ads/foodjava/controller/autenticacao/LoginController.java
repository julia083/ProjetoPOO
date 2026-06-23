package br.edu.ifpb.ads.foodjava.controller.autenticacao;

import br.edu.ifpb.ads.foodjava.controller.cliente.CardapioController;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.Mensagem;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.repository.ClienteRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    /**
     * Controlador da tela de Login
     * Responsável por autenticar clientes e gerente
     */

    // ===== Componentes da tela (ligados ao FXML) =====
    @FXML
    private TextField emailField;          // campo de email

    @FXML
    private PasswordField senhaField;      // campo de senha

    @FXML
    private Hyperlink cadastroClienteLink; // link para tela de cadastro

    // ===== Persistência =====
    private final ClienteRepository clienteRepository = new ClienteRepository();

    // Referência ao restaurante (com o gerente) — ainda sem Repository próprio
    private static Restaurante restaurante;

    /**
     * Método chamado automaticamente quando a tela é carregada
     */

    @FXML
    public void initialize() {
        if (restaurante == null) {
            RestauranteRepository repo = new RestauranteRepository();
            restaurante = repo.buscar();
        }
        System.out.println("Tela de login carregada. Restaurante: " +
                (restaurante != null ? restaurante.getNomeFantasia() : "não configurado"));
    }
    /**
     * Método estático para definir o restaurante/gerente (usado na configuração inicial)
     * @param rest Restaurante configurado
     */
    public static void setRestaurante(Restaurante rest) {
        restaurante = rest;
        System.out.println("Restaurante configurado: " + rest.getNomeFantasia());
    }

    /**
     * Ação do botão "Entrar"
     * Autentica o usuário (cliente ou gerente) e redireciona para a tela correta
     */
    @FXML
    void entrar(ActionEvent event) {
        // Pega os valores digitados
        String email = emailField.getText();
        String senha = senhaField.getText();

        // Validação básica: campos não podem estar vazios
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            Mensagem.mostrarAlerta("Erro de Login", "Preencha todos os campos.");
            return;
        }

        // --- 1ª TENTATIVA: Autenticar como CLIENTE (via Repository) ---
        Optional<Cliente> clienteOpt = clienteRepository.buscarPorEmail(email);
        if (clienteOpt.isPresent() && clienteOpt.get().autenticar(email, senha)) {
            abrirTelaCliente(event, clienteOpt.get());
            return;
        }

        // --- 2ª TENTATIVA: Autenticar como GERENTE ---
        if (restaurante != null && restaurante.getGerente() != null) {
            Gerente gerente = restaurante.getGerente();
            if (gerente.autenticar(email, senha)) {
                abrirTelaGerente(event);
                return;
            }
        }

        // --- NENHUM AUTENTICOU: erro ---
        Mensagem.mostrarAlerta("Erro de Login", "E-mail ou senha inválidos.");
    }

    /**
     * Abre a tela principal do cliente (cardápio)
     */
    private void abrirTelaCliente(ActionEvent event, Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/cardapio.fxml")
            );
            Parent root = loader.load();

            // Passa os dados para o CardapioController
            CardapioController controller = loader.getController();
            controller.setRestaurante(restaurante);
            controller.setClienteLogado(cliente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("FoodJava - Cardápio");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Não foi possível abrir o cardápio.");
        }
    }

    /**
     * Abre o painel do gerente
     */
    private void abrirTelaGerente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/painel-gerente.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("FoodJava - Painel do Gerente");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Não foi possível abrir o painel do gerente.");
        }
    }

    /**
     * Ação do link "Cadastrar cliente"
     * Abre a tela de cadastro de cliente
     */
    @FXML
    void abrirCadastroCliente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/cadastro-cliente.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("FoodJava - Cadastro de Cliente");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Não foi possível abrir a tela de cadastro.");
        }
    }

    public static Restaurante getRestaurante() {
        return restaurante;
    }
}