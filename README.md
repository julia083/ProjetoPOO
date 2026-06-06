# JAVA FOOD
# FoodJava

Sistema de gerenciamento de restaurante desenvolvido em Java como projeto final da disciplina de Programação Orientada a Objetos.

## Integrantes

* Julia Sousa de Lima - 202525020037
* Joel Ferreira Meneses - 202525020030

## Descrição

O FoodJava é uma aplicação desktop desenvolvida em JavaFX para auxiliar no gerenciamento de restaurantes. O sistema permite o cadastro de usuários, gerenciamento de cardápio, realização de pedidos e acompanhamento do fluxo de atendimento.

O projeto foi desenvolvido aplicando conceitos de Programação Orientada a Objetos, persistência de dados em JSON e arquitetura MVC.

## Objetivos

* Aplicar os conceitos de Programação Orientada a Objetos.
* Desenvolver uma aplicação utilizando JavaFX.
* Implementar persistência de dados com JSON.
* Utilizar boas práticas de organização e estruturação de código.

## Funcionalidades

### Cliente

* Cadastro de usuário.
* Login no sistema.
* Visualização do cardápio.
* Realização de pedidos.
* Consulta do histórico de pedidos.
* Acompanhamento do status dos pedidos.

### Gerente

* Login administrativo.
* Gerenciamento do cardápio.
* Cadastro de novos itens.
* Edição e remoção de itens existentes.
* Controle da disponibilidade dos produtos.
* Gerenciamento dos pedidos recebidos.
* Atualização do status dos pedidos.

## Tecnologias Utilizadas

* Java 17
* JavaFX
* Maven
* Gson
* JSON
* Git
* GitHub

## Estrutura do Projeto

```text
src
├── main
│   ├── java
│   │   └── br.edu.ifpb.ads.foodjava
│   │       ├── controller
│   │       ├── model
│   │       ├── repository
│   │       ├── exception
│   │       ├── interfaces
│   │       └── util
│   └── resources
│       ├── fxml
│       ├── css
│       ├── images
│       ├── data
│       └── uploads
```

## Conceitos de POO Aplicados

* Encapsulamento
* Herança
* Polimorfismo
* Abstração
* Interfaces
* Tratamento de exceções personalizadas

## Execução do Projeto

### Clonar o repositório

```bash
git clone https://github.com/julia083/ProjetoPOO.git
```

### Acessar a pasta do projeto

```bash
cd FoodJava
```

### Executar a aplicação

```bash
./mvnw javafx:run
```

ou

```bash
mvn javafx:run
```

## Persistência de Dados

Os dados da aplicação são armazenados em arquivos JSON, permitindo a manutenção das informações entre diferentes execuções do sistema.

## Licença

Projeto acadêmico desenvolvido exclusivamente para fins educacionais.
