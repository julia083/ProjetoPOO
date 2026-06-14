package br.edu.ifpb.ads.foodjava.controller.autenticacao;
import br.edu.ifpb.ads.foodjava.controller.cliente.CardapioController;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        // ===== Dados temporários em memória (enquanto não tem Repository) =====
        // SIMULAÇÃO: lista de clientes cadastrados
        private static List<Cliente> clientesCadastrados = new ArrayList<>();

        // Referência ao restaurante (com o gerente)
        private static Restaurante restaurante;

        /**
         * Método chamado automaticamente quando a tela é carregada
         * Carrega os dados salvos (simulação)
         */
        @FXML
        public void initialize() {
            // Tenta carregar os dados existentes (simulação)
            carregarDadosSimulados();
        }

        /**
         * SIMULAÇÃO: carrega dados previamente salvos
         * Em produção, isso viria do Repository
         */
        private void carregarDadosSimulados() {
            // Se já tem dados carregados, não recarrega
            if (restaurante != null) {
                return;
            }

            // Tenta carregar do sistema (arquivo JSON no futuro)
            // Por enquanto, fica vazio - os dados vêm do cadastro
            System.out.println("Sistema iniciado. Aguardando cadastros...");
        }

        /**
         * Método estático para adicionar um cliente (usado pelo CadastroClienteController)
         * @param cliente Cliente a ser adicionado
         */
        public static void adicionarCliente(Cliente cliente) {
            clientesCadastrados.add(cliente);
            System.out.println("Cliente cadastrado: " + cliente.getEmail());
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
                mostrarAlerta("Erro de Login", "Preencha todos os campos.");
                return;
            }

            // --- 1º TENTATIVA: Autenticar como CLIENTE ---
            for (Cliente cliente : clientesCadastrados) {
                if (cliente.autenticar(email, senha)) {
                    // Cliente autenticado com sucesso!
                    abrirTelaCliente(event, cliente);
                    return;
                }
            }

            // --- 2º TENTATIVA: Autenticar como GERENTE ---
            if (restaurante != null && restaurante.getGerente() != null) {
                Gerente gerente = restaurante.getGerente();
                if (gerente.autenticar(email, senha)) {
                    // Gerente autenticado com sucesso!
                    abrirTelaGerente(event);
                    return;
                }
            }

            // --- NENHUM AUTENTICOU: erro ---
            mostrarAlerta("Erro de Login", "E-mail ou senha inválidos.");
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
                // FUTURO: controller.setClienteLogado(cliente);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("FoodJava - Cardápio");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Não foi possível abrir o cardápio.");
            }
        }

        /**
         * Abre o painel do gerente
         */
        private void abrirTelaGerente(ActionEvent event) {
            try {
                // Carrega o arquivo FXML do painel do gerente
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/painel-gerente.fxml")
                );
                Parent root = loader.load();

                // Obtém a janela atual e troca a cena
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("FoodJava - Painel do Gerente");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Não foi possível abrir o painel do gerente.");
            }
        }

        /**
         * Ação do link "Cadastrar cliente"
         * Abre a tela de cadastro de cliente
         */
        @FXML
        void abrirCadastroCliente(ActionEvent event) {
            try {
                // Carrega o arquivo FXML do cadastro de cliente
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/cadastro-cliente.fxml")
                );
                Parent root = loader.load();

                // Obtém a janela atual e troca a cena
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("FoodJava - Cadastro de Cliente");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Não foi possível abrir a tela de cadastro.");
            }
        }
    /**
     * Método estático para acessar a lista de clientes (usado pelo CadastroClienteController)
     * @return Lista de clientes cadastrados
     */
    public static List<Cliente> getClientesCadastrados() {
        return clientesCadastrados;
    }
        /**
         * Método utilitário para exibir alertas na tela
         */
        private void mostrarAlerta(String titulo, String mensagem) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        }

    public static Restaurante getRestaurante() {
        return restaurante;
    }
    }

