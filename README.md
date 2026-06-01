# Loja Web — Sistema de E-commerce (Spring MVC + JPA + Security + Thymeleaf)

Sistema Web de loja virtual desenvolvido com **Spring Boot 3.3**, **Spring MVC**,
**Spring Data JPA**, **Spring Security** e **Thymeleaf**, empacotado com **Maven**.
Implementa os requisitos descritos em [`REQUISITOS.md`](REQUISITOS.md) (atividades
AA-1 e AA-2), incluindo **login de administrador e de cliente**.

## Como executar

```bash
./mvnw spring-boot:run        # Linux/macOS
.\mvnw spring-boot:run        # Windows (PowerShell)
```

A aplicação sobe em <http://localhost:8080>. O banco é **H2 em memória** (recriado
a cada inicialização), então os dados de exemplo são repopulados sempre.

> Se a porta 8080 estiver ocupada, rode em outra porta:
> `.\mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"`

## Credenciais (populadas na inicialização)

| Perfil        | E-mail              | Senha        |
|---------------|---------------------|--------------|
| Administrador | `admin@loja.com`    | `123`        |
| Cliente       | `cliente@loja.com`  | `123`        |

Novos clientes podem se cadastrar em `/registro`.

## Principais URLs

| URL | Acesso | Descrição |
|-----|--------|-----------|
| `/produtos` | Público | Catálogo com filtro por nome, categoria e faixa de preço (R4) |
| `/login`, `/registro` | Público | Autenticação e auto-cadastro de cliente |
| `/carrinho`, `/pedidos` | Cliente | Carrinho/checkout (R5) e meus pedidos (R6) |
| `/admin/clientes` | Admin | CRUD de clientes (R1) |
| `/admin/categorias` | Admin | CRUD de categorias (R2) |
| `/admin/produtos` | Admin | CRUD de produtos (R3) |
| `/admin/pedidos` | Admin | Gestão de status dos pedidos (R7) |
| `/api/**` | Público | REST-API (AA-2) — ver [`api.http`](api.http) |
| `/h2-console` | Público | Console do banco H2 (URL: `jdbc:h2:mem:lojadb`, user `sa`) |

Troca de idioma (R8): adicione `?lang=pt` ou `?lang=en` à URL.

## Funcionalidades

- **R1–R3:** CRUD de clientes, categorias e produtos (área administrativa).
- **R4:** Catálogo público com filtros combináveis.
- **R5:** Pedido pelo cliente (carrinho + checkout), com decremento de estoque e
  notificação por **e-mail simulado** (exibido no console).
- **R6:** Cliente visualiza seus pedidos com status e total.
- **R7:** Administrador atualiza o status dos pedidos (ABERTO → PAGO → ENVIADO /
  CANCELADO), notificando o cliente por e-mail simulado.
- **R8:** Internacionalização Português/Inglês.
- **R9:** Validação de formulários (Bean Validation) com mensagens traduzidas.
- **Tratamento de erros:** página amigável (`error.html`) para o site e respostas
  JSON com status apropriado para a REST-API; erros registrados no console.

## REST-API (AA-2)

Endpoints sob `/api` (sem autenticação): CRUD de `/api/clientes`, `/api/categorias`
e `/api/produtos`; `GET /api/produtos/categorias/{id}`;
`POST/GET /api/pedidos/clientes/{id}`; `GET /api/pedidos/status/{status}`.
Exemplos prontos para testar (extensão *REST Client* do VS Code) em
[`api.http`](api.http).

## Tecnologias

Spring Boot 3.3 · Spring MVC · Spring Data JPA · Spring Security · Thymeleaf ·
Bean Validation · H2 (em memória) · Java 17 · Maven.
