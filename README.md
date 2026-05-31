# Loja Web — Sistema Web com Spring MVC, Thymeleaf e JPA (T5)

Sistema Web para o domínio de **Loja / E-commerce**, desenvolvido com **Spring Boot 3**,
**Spring MVC**, **Thymeleaf** e **Spring Data JPA**, disponibilizando também uma **REST-API**.

Atividade **T5**: implementação das classes de acesso e manipulação do banco de dados
relacional (entidades e DAOs aderentes à especificação JPA) e uma classe
`@SpringBootApplication` que realiza as operações básicas de CRUD sobre instâncias de exemplo.

## Tecnologias

- Java 17
- Spring Boot 3.3.5 (Spring MVC + Spring Data JPA + Thymeleaf + Validation)
- Banco de dados H2 (em memória) — nenhuma instalação externa necessária
- Maven (com Maven Wrapper)

## Modelo de domínio (entidades JPA)

| Entidade     | Relacionamentos                                            |
|--------------|------------------------------------------------------------|
| `Categoria`  | 1:N com `Produto`                                          |
| `Produto`    | N:1 com `Categoria`                                        |
| `Cliente`    | 1:N com `Pedido`                                           |
| `Pedido`     | N:1 com `Cliente`, 1:N com `ItemPedido`                    |
| `ItemPedido` | N:1 com `Pedido`, N:1 com `Produto`                        |

## DAOs (Spring Data JPA)

`CategoriaRepository`, `ProdutoRepository`, `ClienteRepository` e `PedidoRepository`,
todos estendendo `JpaRepository`, com *query methods* derivados e consultas JPQL.

## Estrutura do projeto

```
src/main/java/com/exemplo/loja
├── LojaWebApplication.java        # @SpringBootApplication + CommandLineRunner (demo CRUD)
├── model/                         # Entidades JPA
│   ├── Categoria.java
│   ├── Produto.java
│   ├── Cliente.java
│   ├── Pedido.java
│   └── ItemPedido.java
├── repository/                    # DAOs (Spring Data JPA)
│   ├── CategoriaRepository.java
│   ├── ProdutoRepository.java
│   ├── ClienteRepository.java
│   └── PedidoRepository.java
├── rest/                          # REST-API
│   ├── ProdutoRestController.java
│   └── CategoriaRestController.java
└── web/                           # Spring MVC + Thymeleaf
    └── ProdutoController.java
src/main/resources
├── application.properties
└── templates/produtos.html
```

## Como executar

> Requer **JDK 17+** instalado. O Maven é baixado automaticamente pelo *wrapper*.

Linux / macOS:
```bash
./mvnw spring-boot:run
```

Windows (PowerShell ou CMD):
```bat
mvnw.cmd spring-boot:run
```

Ao iniciar, o `CommandLineRunner` executa a demonstração de CRUD e imprime o resultado
no console.

## Endpoints

| Recurso                | URL                                      |
|------------------------|------------------------------------------|
| Página Web (Thymeleaf) | http://localhost:8080/produtos           |
| REST-API de produtos   | http://localhost:8080/api/produtos       |
| REST-API de categorias | http://localhost:8080/api/categorias     |
| Console do banco H2    | http://localhost:8080/h2-console         |

> No console H2, use a *JDBC URL* `jdbc:h2:mem:lojadb`, usuário `sa` e senha em branco.

### Exemplos de uso da REST-API

```bash
# Listar produtos
curl http://localhost:8080/api/produtos

# Criar categoria
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome":"Perifericos"}'

# Criar produto (informe o id de uma categoria existente)
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Mouse Gamer","descricao":"RGB","preco":150.0,"estoque":20,"categoria":{"id":1}}'

# Atualizar produto
curl -X PUT http://localhost:8080/api/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Mouse Gamer Pro","descricao":"RGB","preco":199.9,"estoque":15,"categoria":{"id":1}}'

# Remover produto
curl -X DELETE http://localhost:8080/api/produtos/1
```

## Build do .jar

```bash
./mvnw clean package
java -jar target/loja-web-1.0.0.jar
```
