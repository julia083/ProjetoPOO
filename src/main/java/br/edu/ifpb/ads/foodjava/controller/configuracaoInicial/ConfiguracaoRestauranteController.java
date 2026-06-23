package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;
import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.SenhaUtil;
import br.edu.ifpb.ads.foodjava.util.ValidadorCPF;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import java.io.File;
import java.io.IOException;


import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;


public class ConfiguracaoRestauranteController {

    @FXML
    private ComboBox<CategoriaCulinaria> cbCategoria;

    @FXML
    private TextField cnpjRestaurante;

    @FXML
    private TextField cpfGerente;

    @FXML
    private TextField emailGerente;

    @FXML
    private TextField enderecoRestaurante;

    @FXML
    private TextField nomeFantasia;

    @FXML
    private TextField nomeGerente;

    @FXML
    private Button salvarRestaurante;

    @FXML
    private Button selecionarLogoTipo;

    @FXML
    private PasswordField senhaGerente;

    @FXML
    private TextField telefoneRestaurante;

    @FXML
    public void initialize() {
        cbCategoria.getItems().addAll(CategoriaCulinaria.values());
    }

    @FXML
    private void selecionarLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Logotipo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            logoPath = ImagemUtil.salvar(arquivo);
        }
    }

    private String logoPath;

    @FXML
    void salvarORestaurante(ActionEvent event) {
        try {
            // --- 1. PEGAR OS VALORES DOS CAMPOS ---
            String nome = nomeGerente.getText();
            String cpf = cpfGerente.getText();
            String email = emailGerente.getText();
            String senha = senhaGerente.getText();
            String endereco = enderecoRestaurante.getText();
            String telefone = telefoneRestaurante.getText();

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

            try {
                ValidadorCPF.validar(cpf);
            } catch (DocumentoInvalidoException e) {
                mostrarAlerta("CPF Inválido", e.getMessage());
                return;
            }

            try {
                SenhaUtil.senhaValida(senhaGerente.getText());
            } catch (SenhaInvalidaException e) {
                mostrarAlerta("Senha inválida", e.getMessage());
                return;
            }

            Gerente gerente = new Gerente(
                    nomeGerente.getText(),
                    emailGerente.getText(),
                    SenhaUtil.hash(senhaGerente.getText()),
                    cpfGerente.getText()
            );
            Restaurante restaurante = new Restaurante(
                    nomeFantasia.getText(),
                    cnpjRestaurante.getText(),
                    enderecoRestaurante.getText(),
                    telefoneRestaurante.getText(),
                    cbCategoria.getValue(),
                    logoPath,
                    gerente
            );

            if (!restaurante.validar()) {
                System.out.println("Dados inválidos.");
                return;
            }
            RestauranteRepository repositorio = new RestauranteRepository();
            repositorio.salvar(restaurante);

            mostrarAlerta("Cadastro Realizado com Sucesso!", nomeFantasia.getText() + " acaba de ser cadastrado.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
        }

        try {
            // 1. Carrega o FXML da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // 2. Pega a janela (Stage) atual a partir do botão que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Define a nova cena na mesma janela
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}