package br.edu.ifpb.ads.foodjava.controller.configuracaoInicial;

import br.edu.ifpb.ads.foodjava.exception.DocumentoInvalidoException;
import br.edu.ifpb.ads.foodjava.exception.SenhaInvalidaException;
import br.edu.ifpb.ads.foodjava.model.Gerente;
import br.edu.ifpb.ads.foodjava.model.Restaurante;
import br.edu.ifpb.ads.foodjava.model.enums.CategoriaCulinaria;
import br.edu.ifpb.ads.foodjava.repository.RestauranteRepository;
import br.edu.ifpb.ads.foodjava.util.DocumentoUtil;
import br.edu.ifpb.ads.foodjava.util.ImagemUtil;
import br.edu.ifpb.ads.foodjava.util.SenhaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static br.edu.ifpb.ads.foodjava.util.Mensagem.mostrarAlerta;

public class ConfiguracaoRestauranteController {

    @FXML
    private TextField cpfGerente;

    @FXML
    private TextField emailGerente;

    @FXML
    private TextField nomeGerente;

    @FXML
    private Button salvarRestaurante;

    @FXML
    private Button selecionarLogoTipo;

    @FXML
    private PasswordField senhaGerente;

    private String nomeFantasia;
    private String cnpj;
    private String telefone;
    private String localizacao;
    private String logoPath;
    private CategoriaCulinaria categoriaCulinaria;

    public void setLogo(String logoCaminho) {
        this.logoPath = logoCaminho;
    }

    public void setDadosRestaurante(String nomeFantasia, String cnpj, String telefone, String localizacao,CategoriaCulinaria categoriaCulinaria, String logoPath) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.localizacao = localizacao;
        this.categoriaCulinaria = categoriaCulinaria;
        this.logoPath = logoPath;
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
            this.logoPath = ImagemUtil.salvar(arquivo);
        }
    }

    @FXML
    void salvarORestaurante(ActionEvent event) {
        try {
            String nome = nomeGerente.getText();
            String cpf = cpfGerente.getText();
            String email = emailGerente.getText();
            String senha = senhaGerente.getText();

            if (!dadosRestaurantePreenchidos()) {
                mostrarAlerta("Erro de Validação", "Dados do restaurante não foram informados corretamente.");
                return;
            }
            if (campoVazio(nome)) {
                mostrarAlerta("Erro de Validação", "O campo Nome é obrigatório.");
                return;
            }
            if (campoVazio(cpf)) {
                mostrarAlerta("Erro de Validação", "O campo CPF é obrigatório.");
                return;
            }
            if (campoVazio(email)) {
                mostrarAlerta("Erro de Validação", "O campo Email é obrigatório.");
                return;
            }
            if (campoVazio(senha)) {
                mostrarAlerta("Erro de Validação", "O campo Senha é obrigatório.");
                return;
            }

            try {
                DocumentoUtil.validarCpf(cpf);
            } catch (DocumentoInvalidoException e) {
                mostrarAlerta("CPF Inválido", e.getMessage());
                return;
            }

            try {
                SenhaUtil.senhaValida(senha);
            } catch (SenhaInvalidaException e) {
                mostrarAlerta("Senha inválida", e.getMessage());
                return;
            }

            Gerente gerente = new Gerente(
                    nome.trim(),
                    email.trim(),
                    SenhaUtil.hash(senha),
                    cpf.trim()
            );

            Restaurante restaurante = new Restaurante(
                    nomeFantasia,
                    cnpj,
                    localizacao,
                    telefone,
                    categoriaCulinaria,
                    logoPath,
                    gerente
            );

            if (!restaurante.validar()) {
                mostrarAlerta("Erro de Validação", "Dados inválidos para cadastro do restaurante.");
                return;
            }

            RestauranteRepository repositorio = new RestauranteRepository();
            repositorio.salvar(restaurante);

            mostrarAlerta("Cadastro Realizado com Sucesso!", nomeFantasia + " acaba de ser cadastrado.");
            abrirLogin(event);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    private void abrirLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Restaurante cadastrado, mas não foi possível abrir a tela de login.");
        }
    }

    private boolean dadosRestaurantePreenchidos() {
        return !campoVazio(nomeFantasia)
                && !campoVazio(cnpj)
                && !campoVazio(telefone)
                && !campoVazio(localizacao)
                && categoriaCulinaria != null;
    }

    private boolean campoVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
