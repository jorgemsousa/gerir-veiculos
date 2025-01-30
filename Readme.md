# API de Gerenciamento de Veículos

Esta é uma API REST desenvolvida em **Java** usando **JDBC** para conectar-se a um banco de dados **PostgreSQL**. A API permite realizar operações CRUD (Create, Read, Update, Delete) em veículos, diferenciando entre carros e motos.

## 📌 Tecnologias Utilizadas

- Java 17+
- JDBC (Java Database Connectivity)
- PostgreSQL
- Spring Boot (para estruturação do projeto)
- Postman / Insomnia (para testes de API)

## 📁 Estrutura do Banco de Dados

A API gerencia os seguintes dados:

### 🏛️ \*\*Tabela: \*\*`` (Tabela Principal)

| Campo      | Tipo          | Restrições            |
| ---------- | ------------- | --------------------- |
| id         | SERIAL        | PRIMARY KEY           |
| modelo     | VARCHAR(255)  | NOT NULL              |
| fabricante | VARCHAR(255)  | NOT NULL              |
| ano        | INT           | NOT NULL              |
| preco      | DECIMAL(10,2) | NOT NULL              |
| cor        | VARCHAR(50)   | NOT NULL              |
| tipo       | VARCHAR(10)   | NOT NULL (carro/moto) |

### 🚗 \*\*Tabela: \*\*`` (Apenas para Veículos do Tipo "Carro")

| Campo            | Tipo        | Restrições                                |
| ---------------- | ----------- | ----------------------------------------- |
| id               | INT         | PRIMARY KEY, FOREIGN KEY (veiculos)       |
| quantidadePortas | INT         | NOT NULL                                  |
| tipoCombustivel  | VARCHAR(50) | NOT NULL (gasolina, etanol, diesel, flex) |

### 🏍️ \*\*Tabela: \*\*`` (Apenas para Veículos do Tipo "Moto")

| Campo      | Tipo | Restrições                          |
| ---------- | ---- | ----------------------------------- |
| id         | INT  | PRIMARY KEY, FOREIGN KEY (veiculos) |
| cilindrada | INT  | NOT NULL                            |

---

## 🚀 **Endpoints da API**

### 1️⃣ Criar um Veículo

#### ``

**Requisição (JSON):**

```json
{
  "modelo": "Civic",
  "fabricante": "Honda",
  "ano": 2023,
  "preco": 120000.00,
  "cor": "Preto",
  "tipo": "CARRO",
  "quantidadePortas": 4,
  "tipoCombustivel": "FLEX"
}
```

**Resposta (201 Created):**

```json
{"message": "Veículo adicionado com sucesso!"}
```

---

### 2️⃣ Listar Todos os Veículos (com filtros opcionais)

#### ``

**Parâmetros opcionais:** `tipo`, `cor`, `modelo`, `ano`

**Exemplo de chamada:**

```http
GET /api/veiculos?tipo=carro&cor=preto
```

**Resposta (200 OK):**

```json
[
  {
    "id": 1,
    "modelo": "Civic",
    "fabricante": "Honda",
    "ano": 2023,
    "preco": 120000.00,
    "cor": "Preto",
    "tipo": "CARRO",
    "quantidadePortas": 4,
    "tipoCombustivel": "FLEX"
  }
]
```

---

### 3️⃣ Buscar Veículo por ID

#### ``

**Exemplo de chamada:**

```http
GET /api/veiculos/1
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "modelo": "Civic",
  "fabricante": "Honda",
  "ano": 2023,
  "preco": 120000.00,
  "cor": "Preto",
  "tipo": "CARRO",
  "quantidadePortas": 4,
  "tipoCombustivel": "FLEX"
}
```

---

### 4️⃣ Editar um Veículo

#### ``

**Requisição (JSON):**

```json
{
  "modelo": "Civic",
  "fabricante": "Honda",
  "ano": 2023,
  "preco": 125000.00,
  "cor": "Branco",
  "tipo": "CARRO",
  "quantidadePortas": 4,
  "tipoCombustivel": "FLEX"
}
```

**Resposta (200 OK):**

```json
{"message": "Veículo atualizado com sucesso!"}
```

---

### 5️⃣ Deletar um Veículo

#### ``

**Exemplo de chamada:**

```http
DELETE /api/veiculos/1
```

**Resposta (200 OK):**

```json
{"message": "Veículo deletado com sucesso!"}
```

---

## 🛠️ **Como Executar a API**

### 1️⃣ Configurar o Banco de Dados PostgreSQL

```sql
CREATE DATABASE veiculos_db;
```

### 2️⃣ Configurar a Conexão com o Banco

No arquivo `DatabaseConfig.java`, defina:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/veiculos_db";
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

### 3️⃣ Executar o Servidor

```sh
mvn spring-boot:run
```

A API estará rodando em `http://localhost:8080/api/veiculos` 🚀

### 4️⃣ Eecutar o Front
O Front estará rodando em `http://localhost:8080/index.html` 🚀

---

## 📌 **Conclusão**

Essa API permite **gerenciar veículos**, diferenciando entre **carros e motos**, utilizando **JDBC com consultas nativas ao PostgreSQL**. Ideal para sistemas de controle de frotas, lojas de veículos, entre outros! 🚗🏍️

É primordial que utilizamos letras em caixa nos objetos passados apenas nos campos Tipo\_Combustivel e Tipo conforme exemplos acima, pois, criamos ENUM no java que por padrão utilizamos caixa alta no conjunto. \
