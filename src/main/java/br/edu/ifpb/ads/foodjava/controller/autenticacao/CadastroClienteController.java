package br.edu.ifpb.ads.foodjava.controller.autenticacao;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;
import br.edu.ifpb.ads.foodjava.exception.UsuarioDuplicadoException;
import br.edu.ifpb.ads.foodjava.model.Cliente;
import br.edu.ifpb.ads.foodjava.repository.ClienteRepository;
import br.edu.ifpb.ads.foodjava.util.DocumentoUtil;
import br.edu.ifpb.ads.foodjava.util.Mensagem;
import br.edu.ifpb.ads.foodjava.util.SenhaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CadastroClienteController {

    @FXML private TextField nomeField;
    @FXML private TextField cpfField;
    @FXML private TextField emailField;
    @FXML private TextField telefoneField;
    @FXML private Label telefoneAvisoLabel;
    @FXML private PasswordField senhaField;
    @FXML private TextField enderecoField;
    @FXML private Button voltarLoginButton;
    @FXML private Button limparFormularioButton;
    @FXML private Button cadastrarClienteButton;

    private final ClienteRepository clienteRepository = new ClienteRepository();

    @FXML
    public void initialize() {
        telefoneAvisoLabel.setVisible(false);
    }

    @FXML
    void voltarLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FoodJava - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Nao foi possivel voltar para o login.");
        }
    }

    @FXML
    void limparFormulario(ActionEvent event) {
        nomeField.clear();
        cpfField.clear();
        emailField.clear();
        telefoneField.clear();
        senhaField.clear();
        enderecoField.clear();
        telefoneAvisoLabel.setVisible(false);
    }

    @FXML
    void cadastrarCliente(ActionEvent event) {
        try {
            String nome = nomeField.getText();
            String cpf = cpfField.getText();
            String email = emailField.getText();
            String telefone = telefoneField.getText();
            String senha = senhaField.getText();
            String endereco = enderecoField.getText();

            telefoneAvisoLabel.setVisible(false);

            if (nome == null || nome.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo Nome e obrigatorio.");
                return;
            }
            if (cpf == null || cpf.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo CPF e obrigatorio.");
                return;
            }
            if (email == null || email.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo Email e obrigatorio.");
                return;
            }
            if (telefone == null || telefone.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo Telefone e obrigatorio.");
                return;
            }
            if (!telefoneValido(telefone)) {
                telefoneAvisoLabel.setText("Informe pelo menos 10 digitos.");
                telefoneAvisoLabel.setVisible(true);
                return;
            }
            if (senha == null || senha.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo Senha e obrigatorio.");
                return;
            }
            if (endereco == null || endereco.isBlank()) {
                Mensagem.mostrarAlerta("Erro de Validacao", "O campo Endereco e obrigatorio.");
                return;
            }

            try {
                DocumentoUtil.validarCpf(cpf);
            } catch (DocumentoInvalidoException e) {
                Mensagem.mostrarAlerta("CPF Invalido", e.getMessage());
                return;
            }

            try {
                SenhaUtil.senhaValida(senha);
            } catch (SenhaInvalidaException e) {
                Mensagem.mostrarAlerta("Senha invalida", e.getMessage());
                return;
            }

            Cliente novoCliente = new Cliente(nome, email, SenhaUtil.hash(senha), telefone, cpf, endereco);
            clienteRepository.cadastrar(novoCliente);

            Mensagem.mostrarAlerta("Cadastro Realizado", "Cliente cadastrado com sucesso! Faca login para continuar.");
            voltarLogin(event);
        } catch (UsuarioDuplicadoException e) {
            Mensagem.mostrarAlerta("Cadastro Duplicado", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Mensagem.mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    private boolean telefoneValido(String telefone) {
        String somenteDigitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        return somenteDigitos.length() >= 10;
    }
}
