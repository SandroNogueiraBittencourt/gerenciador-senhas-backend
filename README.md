# 🔐 Gerenciador de Senhas — Backend

Backend do projeto acadêmico **Gerenciador de Senhas**, desenvolvido em **Java com Spring Boot**.

Este repositório contém a API REST responsável por autenticação, regras de negócio, persistência dos dados, segurança e comunicação com o banco de dados **MariaDB**.

---

## 📌 Sobre o Projeto

O sistema tem como objetivo permitir que usuários autenticados possam cadastrar, listar, editar, excluir, mostrar e ocultar senhas associadas a sites ou aplicativos.

Este backend será consumido por uma aplicação frontend desenvolvida em **React**, mantida em outro repositório.

---

## 🚀 Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- Maven
- MariaDB
- BCrypt
- JWT, caso seja adotado no projeto

---

## 🏗️ Arquitetura

```text
Frontend React  →  API REST Spring Boot  →  Banco de Dados MariaDB
```

Este repositório representa apenas a camada de backend da aplicação.

---

## 📁 Estrutura do Projeto

```text
gerenciador-senhas-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gerenciador/senhas/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       ├── dto/
│   │   │       ├── mapper/
│   │   │       ├── util/
│   │   │       ├── security/
│   │   │       └── config/
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

---

## 📦 Organização das Pastas

### `controller/`

Responsável por receber requisições HTTP e retornar respostas da API REST.

Exemplos:

- `AuthController`
- `UsuarioController`
- `SenhaController`
- `CategoriaController`

---

### `service/`

Responsável pelas regras de negócio da aplicação.

Exemplos:

- Validar dados;
- Verificar proprietário do registro;
- Criptografar senhas;
- Chamar os repositórios.

---

### `repository/`

Responsável pela comunicação com o banco de dados usando **Spring Data JPA**.

Exemplos:

- `UsuarioRepository`
- `SenhaRepository`
- `CategoriaRepository`

---

### `entity/`

Contém as entidades JPA que representam as tabelas do banco de dados.

Exemplos:

- `Usuario`
- `Senha`
- `Categoria`

---

### `dto/`

Contém objetos usados para entrada e saída de dados da API.

Exemplos:

- `LoginRequestDTO`
- `RegisterRequestDTO`
- `SenhaRequestDTO`
- `SenhaResponseDTO`

---

### `mapper/`

Responsável por converter entidades em DTOs e DTOs em entidades.

Exemplos:

- `SenhaRequestDTO` para `Senha`
- `Senha` para `SenhaResponseDTO`

---

### `util/`

Contém classes auxiliares do sistema.

Exemplos:

- Criptografia de senhas;
- Validação de força de senha;
- Geração de senhas fortes;
- Utilitários de data.

---

### `security/`

Contém as configurações e classes de segurança.

Exemplos:

- Configuração do Spring Security;
- Filtros de autenticação;
- JWT;
- Detalhes do usuário autenticado.

---

### `config/`

Contém configurações gerais da aplicação.

Exemplos:

- CORS;
- Beans globais;
- Configurações de criptografia.

---

## 🗄️ Banco de Dados

O banco utilizado no projeto é o **MariaDB**.

### Criação do banco

```sql
CREATE DATABASE gerenciador_senhas;
```

---

## ⚙️ Configuração do `application.properties`

Exemplo de configuração:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/gerenciador_senhas
spring.datasource.username=root
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## 🧱 Entidades Principais

### Usuário

Representa o usuário cadastrado no sistema.

Campos principais:

- id
- nome
- email
- senha_hash
- data_criacao

### Senha

Representa uma senha cadastrada pelo usuário.

Campos principais:

- id
- nome_servico
- url
- login_servico
- senha_criptografada
- observacoes
- data_criacao
- data_atualizacao
- usuario_id
- categoria_id

### Categoria

Representa uma categoria para organização das senhas.

Campos principais:

- id
- nome
- descricao
- usuario_id

---

## 🔗 Endpoints Sugeridos

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/register` | Cadastrar usuário |
| POST | `/auth/login` | Realizar login |
| POST | `/auth/logout` | Encerrar sessão |

### Senhas

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/passwords` | Listar senhas do usuário autenticado |
| GET | `/passwords/{id}` | Buscar senha por ID |
| POST | `/passwords` | Cadastrar senha |
| PUT | `/passwords/{id}` | Atualizar senha |
| DELETE | `/passwords/{id}` | Excluir senha |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/categories` | Listar categorias |
| POST | `/categories` | Cadastrar categoria |
| PUT | `/categories/{id}` | Atualizar categoria |
| DELETE | `/categories/{id}` | Excluir categoria |

---

## 🔒 Segurança

O backend deve implementar:

- Criptografia da senha de login com BCrypt;
- Proteção de rotas com Spring Security;
- Validação do usuário autenticado;
- Isolamento dos dados por usuário;
- Proteção contra acesso indevido aos registros de outro usuário;
- Criptografia das senhas cadastradas no gerenciador.

---

## ▶️ Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven
- MariaDB
- Git

### Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO_BACKEND
cd gerenciador-senhas-backend
```

### Executar o projeto

```bash
mvn spring-boot:run
```

A API será executada em:

```text
http://localhost:8080
```

---

## 🧪 Testes Sugeridos

- Cadastro de usuário;
- Login com credenciais válidas;
- Login com credenciais inválidas;
- Cadastro de senha;
- Listagem de senhas;
- Edição de senha;
- Exclusão de senha;
- Busca por senha;
- Validação de acesso por usuário;
- Bloqueio de acesso sem autenticação.

---

## 📌 Status

```text
🚧 Em desenvolvimento
```

---

## 👨‍💻 Autor

**Sandro Nogueira**

- GitHub: [SandroNogueiraBittencourt](https://github.com/SandroNogueiraBittencourt)

---

## 📚 Observação

Este projeto possui finalidade acadêmica e não deve ser utilizado em produção sem revisão de segurança, auditoria de código e testes de vulnerabilidade.
