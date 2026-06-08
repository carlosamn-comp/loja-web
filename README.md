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

---

## Estrutura do projeto (onde está o quê)

Pacote base: `com.exemplo.loja`.

```
src/main/java/com/exemplo/loja/
├── LojaWebApplication.java      # classe @SpringBootApplication (ponto de entrada / main)
│
├── model/                       # ENTIDADES JPA (@Entity) — o "M" do MVC
│   ├── Usuario.java             # classe-mãe ABSTRATA (herança SINGLE_TABLE); campos
│   │                            #   comuns: nome, email (único), senha, role
│   ├── Loja.java                # extends Usuario; role "LOJA" no construtor; + cnpj, descricao
│   ├── Cliente.java             # extends Usuario; role "CLIENTE" no construtor; + cpf, sexo...
│   ├── Categoria.java           # categoria de produtos (1:N com Produto)
│   ├── Produto.java             # pertence a uma Loja e a uma Categoria
│   ├── ProdutoImagem.java       # imagem do produto (bytes/BLOB no banco) — máx. 10
│   ├── Pedido.java              # pedido do cliente (N:1 Cliente, 1:N ItemPedido) + enum Status
│   ├── ItemPedido.java          # item do pedido (produto + quantidade)
│   └── Sexo.java                # enum MASCULINO/FEMININO/OUTRO
│
├── repository/                  # DAOs (Spring Data JPA) — interfaces JpaRepository
│   ├── UsuarioRepository.java   # busca por e-mail p/ login e unicidade (abrange loja+cliente)
│   ├── LojaRepository, ClienteRepository, CategoriaRepository,
│   ├── ProdutoRepository.java   # + query @Query "filtrar" (nome/categoria/loja/preço) R4
│   ├── ProdutoImagemRepository, PedidoRepository.java  # PedidoRepo usa @EntityGraph
│
├── service/                     # REGRAS DE NEGÓCIO (@Service)
│   ├── UsuarioDetailsService.java  # AUTENTICAÇÃO: carrega loja/cliente do banco (1 consulta, usa a role)
│   ├── LojaService, ClienteService, CategoriaService,
│   ├── ProdutoService.java      # CRUD + filtros + imagens
│   ├── PedidoService.java       # checkout transacional + atualização de status
│   └── EmailService.java        # e-mail SIMULADO (loga no console)
│
├── web/                         # CONTROLLERS MVC (@Controller) — o "C" do MVC
│   ├── ProdutoController.java   # catálogo público (/produtos) com filtros (R4)
│   ├── AuthController.java      # /login, /registro (cliente), /registro-loja
│   ├── PedidoClienteController.java  # /pedidos (meus pedidos do cliente, R6)
│   ├── ControllerModelAdvice.java    # expõe a URI atual (troca de idioma)
│   ├── admin/                   # área do ADMIN: clientes, lojas, categorias, pedidos
│   ├── loja/                    # área da LOJA: seus produtos (R3) e suas vendas (R7)
│   └── carrinho/                # CarrinhoSession (@SessionScope) + CarrinhoController (R5)
│
├── rest/                        # REST-API (@RestController) — AA-2, sob /api/**
│   └── Cliente/Loja/Categoria/Produto/PedidoRestController.java
│
├── dto/                         # objetos de entrada da REST (PedidoRequest, ItemPedidoRequest)
├── config/
│   ├── SecurityConfig.java      # Spring Security: admin EM MEMÓRIA (ROLE_ADMIN) + provider do
│   │                            #   banco (loja/cliente); acessos por perfil, BCrypt, CSRF, H2
│   ├── WebConfig.java           # i18n (locale + interceptor) e validação localizada
│   └── DataSeeder.java          # carga inicial (cliente, 2 lojas, produtos) — admin NÃO
└── exception/
    ├── GlobalExceptionHandler.java   # erros das telas → error.html
    ├── ApiExceptionHandler.java      # erros da REST → JSON
    └── EstoqueInsuficienteException.java

src/main/resources/
├── application.properties       # H2 em arquivo, JPA, i18n, upload de imagens
├── messages.properties / messages_en.properties   # i18n PT / EN (R8)
├── static/css/estilo.css        # CSS
└── templates/                   # VISÕES Thymeleaf — o "V" do MVC
    ├── fragments/layout.html    # navbar (links por perfil) + barras admin/loja
    ├── produtos.html, login.html, registro.html, registro-loja.html, carrinho.html, error.html
    ├── admin/{clientes,lojas,categorias,pedidos}/   e   loja/{produtos,pedidos}/
    └── pedidos/meus-pedidos.html
```

## Pontos importantes

- **Herança JPA (Aula07):** `Usuario` é a classe-mãe abstrata e `Loja`/`Cliente` são
  subclasses. Estratégia **`SINGLE_TABLE`** → tudo numa tabela `usuario` com a coluna
  discriminadora `tipo`. A role começa como **`"USER"`** (na classe-mãe) e é
  **renomeada** pelo construtor de cada subclasse para **`"LOJA"`/`"CLIENTE"`**, lida
  diretamente no login.
- **Admin fixo no código:** o administrador (`admin@loja.com` / `123`) é um usuário
  **em memória** (`InMemoryUserDetailsManager`) definido em `SecurityConfig`, com
  **ROLE_ADMIN atribuída no login** — **não fica no banco**. O Spring Security usa dois
  `AuthenticationProvider`: um para o admin em memória e outro para os usuários do banco
  (loja/cliente), resolvidos em **uma única consulta** (`findByEmail`) usando a `role`.
- **Marketplace:** cada produto pertence a uma **loja**; um cliente compra de **várias
  lojas** no mesmo pedido. A loja gerencia só os seus produtos e vê só as suas vendas.
- **Imagens no banco:** armazenadas como bytes (BLOB) em `produto_imagem`, servidas por
  `GET /produtos/imagens/{id}` com o content-type correto (máx. 10 por produto).
- **Persistência:** H2 em arquivo (`data/lojadb.mv.db`). Para zerar, pare a app e apague
  a pasta `data/`.
- **CPF/CNPJ:** apenas obrigatórios e únicos (sem validação de formato — é um modelo).
- **Camadas (MVC):** `model` (entidades) → `repository` (DAOs) → `service` (regras) →
  `web`/`rest` (controllers) → `templates` (views). Veja os comentários no topo de cada
  classe para o papel dela.
