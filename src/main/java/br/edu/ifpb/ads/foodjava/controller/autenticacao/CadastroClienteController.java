package br.edu.ifpb.ads.foodjava.controller.autenticacao;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.exception.UsuarioDuplicadoException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.repository.ClienteRepository;
import br.edu.ifpb.ads.foodjava.util.ValidadorCPF;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CadastroClienteController {

    /**
     * Controlador da tela de Cadastro de Cliente
     * Responsável por validar dados e criar novo cliente
     */

    // ===== Componentes da tela (ligados ao FXML) =====
    @FXML
    private TextField nomeField;           // nome completo

    @FXML
    private TextField cpfField;             // CPF

    @FXML
    private TextField emailField;           // email

    @FXML
    private TextField telefoneField;        // telefone

    @FXML
    private PasswordField senhaField;       // senha

    @FXML
    private TextField enderecoField;        // endereço

    @FXML
    private Button voltarLoginButton;       // botão voltar

    @FXML
    private Button limparFormularioButton;  // botão limpar

    @FXML
    private Button cadastrarClienteButton;  // botão cadastrar

    // ===== Persistência =====
    private final ClienteRepository clienteRepository = new ClienteRepository();

    /**
     * Método chamado automaticamente quando a tela é carregada
     */
    @FXML
    public void initialize() {
        System.out.println("Tela de cadastro de cliente carregada.");
    }

    /**
     * Ação do botão "Voltar"
     * Retorna para a tela de login
     */
    @FXML
    void voltarLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("FoodJava - Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível voltar para o login.");
        }
    }

    /**
     * Ação do botão "Limpar"
     * Limpa todos os campos do formulário
     */
    @FXML
    void limparFormulario(ActionEvent event) {
        nomeField.clear();
        cpfField.clear();
        emailField.clear();
        telefoneField.clear();
        senhaField.clear();
        enderecoField.clear();
    }

    /**
     * Ação do botão "Cadastrar"
     * Valida os dados e cria um novo cliente
     */
    @FXML
    void cadastrarCliente(ActionEvent event) {
        try {
            // --- 1. PEGAR OS VALORES DOS CAMPOS ---
            String nome = nomeField.getText();
            String cpf = cpfField.getText();
            String email = emailField.getText();
            String telefone = telefoneField.getText();
            String senha = senhaField.getText();
            String endereco = enderecoField.getText();

            // --- 2. VALIDAÇÃO DE CAMPOS VAZIOS ---
            if (nome == null || nome.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo Nome é obrigatório.");
                return;
            }
            if (cpf == null || cpf.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo CPF é obrigatório.");
                return;
            }
            if (email == null || email.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo Email é obrigatório.");
                return;
            }
            if (telefone == null || telefone.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo Telefone é obrigatório.");
                return;
            }
            if (senha == null || senha.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo Senha é obrigatório.");
                return;
            }
            if (endereco == null || endereco.isBlank()) {
                mostrarAlerta("Erro de Validação", "O campo Endereço é obrigatório.");
                return;
            }

            // --- 3. VALIDAÇÃO DO CPF (usando seu ValidadorCPF) ---
            try {
                ValidadorCPF.validar(cpf);
            } catch (DocumentoInvalidoException e) {
                mostrarAlerta("CPF Inválido", e.getMessage());
                return;
            }

            // --- 4. VALIDAÇÃO DA SENHA ---
            if (!senhaValida(senha)) {
                mostrarAlerta("Senha Inválida",
                        "A senha deve ter pelo menos 8 caracteres e conter um dígito numérico.");
                return;
            }

            // --- 5. CRIAR E PERSISTIR O CLIENTE ---
            // A checagem de e-mail e CPF duplicados agora é feita dentro do Repository
            Cliente novoCliente = new Cliente(nome, email, senha, telefone, cpf, endereco);
            clienteRepository.cadastrar(novoCliente);

            // --- 6. MOSTRAR MENSAGEM DE SUCESSO ---
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Cadastro Realizado");
            alert.setHeaderText(null);
            alert.setContentText("Cliente cadastrado com sucesso! Faça login para continuar.");
            alert.showAndWait();

            // --- 7. VOLTAR PARA TELA DE LOGIN ---
            voltarLogin(event);

        } catch (UsuarioDuplicadoException e) {
            mostrarAlerta("Cadastro Duplicado", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Valida a senha (mesma regra da classe Usuario)
     *
     * @param senha Senha a ser validada
     * @return true se a senha atende aos critérios
     */
    private boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 8 && senha.chars().anyMatch(Character::isDigit);
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
}