package br.edu.ifpb.ads.foodjava.controller.autenticacao;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;
import br.edu.ifpb.ads.foodjava.exception.UsuarioDuplicadoException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.repository.ClienteRepository;
import br.edu.ifpb.ads.foodjava.util.SenhaUtil;
import br.edu.ifpb.ads.foodjava.util.DocumentoUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import br.edu.ifpb.ads.foodjava.util.Mensagem;


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
            Mensagem.mostrarAlerta("Erro", "Não foi possível voltar para o login.");
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
                Mensagem.mostrarAlerta("Erro de Validação", "O campo Nome é obrigatório.");
                return;
            }
            if (cpf == null || cpf.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validação", "O campo CPF é obrigatório.");
                return;
            }
            if (email == null || email.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validação", "O campo Email é obrigatório.");
                return;
            }
            if (telefone == null || telefone.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validação", "O campo Telefone é obrigatório.");
                return;
            }
            if (senha == null || senha.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validação", "O campo Senha é obrigatório.");
                return;
            }
            if (endereco == null || endereco.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validação", "O campo Endereço é obrigatório.");
                return;
            }

            try {
                DocumentoUtil.validarCpf(cpf);
            } catch (DocumentoInvalidoException e) {
                Mensagem.mostrarAlerta("CPF Inválido", e.getMessage());
                return;
            }

            try {
                SenhaUtil.senhaValida(senha);
            } catch (SenhaInvalidaException e) {
                Mensagem.mostrarAlerta("Senha inválida", e.getMessage());
                return;
            }

            // A checagem de e-mail e CPF duplicados é feita dentro do Repository
            Cliente novoCliente = new Cliente(nome, email, SenhaUtil.hash(senha), telefone, cpf, endereco);
            clienteRepository.cadastrar(novoCliente);

            Mensagem.mostrarAlerta("Cadastro Realizado", "Cliente cadastrado com sucesso! Faça login para continuar.");

            voltarLogin(event);

        } catch (UsuarioDuplicadoException e) {
            Mensagem.mostrarAlerta("Cadastro Duplicado", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
        }
    }
}