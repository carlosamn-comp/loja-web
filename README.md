# Marketplace Web (Spring MVC + JPA + Security + Thymeleaf)

Marketplace de produtos (estilo Mercado Livre) desenvolvido com **Spring Boot 3.3**,
**Spring MVC**, **Spring Data JPA**, **Spring Security** e **Thymeleaf**, empacotado
com **Maven**. Várias **lojas/vendedores** cadastram produtos e os **clientes**
compram de diferentes lojas. Implementa os requisitos de [`REQUISITOS.md`](REQUISITOS.md)
(AA-1 e AA-2), com **três perfis de login: administrador, loja e cliente**.

## Como executar

```bash
./mvnw spring-boot:run        # Linux/macOS
.\mvnw spring-boot:run        # Windows (PowerShell)
```

A aplicação sobe em <http://localhost:8080>. O banco é **H2 em arquivo**
(`data/lojadb.mv.db`), então os dados **persistem entre reinicializações**. Para
zerar o banco, pare a aplicação e apague a pasta `data/`.

> Se a porta 8080 estiver ocupada:
> `.\mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"`

## Credenciais (populadas na inicialização — senha `123`)

| Perfil        | E-mail              | Senha |
|---------------|---------------------|-------|
| Administrador | `admin@loja.com`    | `123` |
| Loja          | `loja@loja.com`     | `123` |
| Cliente       | `cliente@loja.com`  | `123` |

Novos clientes em `/registro`; novas lojas em `/registro-loja`.

## Principais URLs

| URL | Acesso | Descrição |
|-----|--------|-----------|
| `/produtos` | Público | Catálogo com filtro por nome, **loja**, categoria e preço (R4) |
| `/login`, `/registro`, `/registro-loja` | Público | Autenticação e auto-cadastro de cliente/loja |
| `/carrinho`, `/pedidos` | Cliente | Carrinho/checkout (R5) e meus pedidos (R6) |
| `/loja/produtos` | Loja | CRUD dos produtos **da própria loja**, com imagens (R3) |
| `/loja/pedidos` | Loja | Vendas da loja + atualização de status (R7) |
| `/admin/clientes` | Admin | CRUD de clientes (R1) |
| `/admin/lojas` | Admin | CRUD de lojas (R2) |
| `/admin/categorias` | Admin | CRUD de categorias |
| `/admin/pedidos` | Admin | Gestão de status de todos os pedidos (R7) |
| `/api/**` | Público | REST-API (AA-2) — ver [`api.http`](api.http) |
| `/h2-console` | Público | Console H2 (URL: `jdbc:h2:file:./data/lojadb`, user `sa`) |

Troca de idioma (R8): adicione `?lang=pt` ou `?lang=en` à URL.

## Funcionalidades

- **R1–R2:** CRUD de clientes e de lojas (administrador).
- **R3:** Cada **loja** cadastra/edita/remove os seus próprios produtos (com upload de
  até 10 imagens, armazenadas no banco como bytes/BLOB).
- **R4:** Catálogo público com filtros combináveis (nome, loja, categoria, preço).
- **R5:** Cliente monta um carrinho com produtos de **várias lojas** e finaliza a compra,
  com decremento de estoque e notificação por **e-mail simulado** (exibido no console).
- **R6:** Cliente visualiza seus pedidos com status e total.
- **R7:** A **loja** vê os pedidos que contêm os seus produtos e atualiza o status; o
  **administrador** também gerencia o status de todos os pedidos. Cliente é notificado.
- **R8:** Internacionalização Português/Inglês.
- **R9:** Validação de formulários (Bean Validation) com mensagens traduzidas.
- **Tratamento de erros:** página amigável (`error.html`) no site e respostas JSON na
  REST-API; erros registrados no console.

## REST-API (AA-2)

Endpoints sob `/api` (sem autenticação): CRUD de `/api/clientes`, `/api/lojas`,
`/api/categorias` e `/api/produtos`; `GET /api/produtos/categorias/{id}`;
`GET /api/produtos/lojas/{id}`; `POST/GET /api/pedidos/clientes/{id}`;
`GET /api/pedidos/status/{status}`. Exemplos prontos (extensão *REST Client* do VS Code)
em [`api.http`](api.http).

## Tecnologias

Spring Boot 3.3 · Spring MVC · Spring Data JPA · Spring Security · Thymeleaf ·
Bean Validation · H2 (arquivo) · Java 17 · Maven.
