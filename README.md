# Marketplace Web (Spring MVC + JPA + Security + Thymeleaf)

Marketplace de produtos (estilo Mercado Livre) desenvolvido com **Spring Boot 3.3**,
**Spring MVC**, **Spring Data JPA**, **Spring Security** e **Thymeleaf**, empacotado
com **Maven**. Várias **lojas/vendedores** cadastram produtos e os **clientes**
compram de diferentes lojas. Implementa os requisitos de [`REQUISITOS.md`](REQUISITOS.md)
(AA-1 e AA-2), com **três perfis de login: administrador, loja e cliente**.

## Sobre as atividades (T6, T7 e T8 — Desenvolvimento de Software para a Web)

> As atividades **T5 a T8** são passos incrementais do ciclo de desenvolvimento de um
> sistema Web. O objetivo geral é **desenvolver um sistema Web usando Spring MVC,
> Thymeleaf e JPA que disponibiliza uma REST-API**, para um domínio escolhido e um
> conjunto de requisitos.
>
> - **T6:** implementar o sistema Web (telas com Spring MVC + Thymeleaf + JPA).
> - **T7:** complementar a T6 implementando a **API REST** (Controladores REST do Spring
>   MVC + Spring JPA) para o mesmo conjunto de requisitos.
> - **T8:** implementar uma aplicação web **cliente da REST-API**, que acessa o CRUD de
>   uma entidade via **RestClient** + Spring MVC + Thymeleaf. Entidade escolhida:
>   **Categoria** (tela em `/rest-client/categorias`).
>
> O domínio escolhido aqui é um **marketplace de produtos**. Este repositório já contempla
> **T6, T7 e T8** (sistema Web + REST-API + cliente da REST-API).

**Obs 1:** uso obrigatório de **Spring MVC + Spring Data JPA + Thymeleaf**.
**Obs 2:** todos os artefatos (controladores, visões, entidades JPA, serviços, etc.)
devem ser adequadamente implementados. Apenas **projetos Maven** configurados são aceitos.
**Entrega:** postar a URL do GitHub do projeto.

### Como o projeto atende à T6

| Exigência da T6 | Onde está no projeto |
|---|---|
| **Spring MVC** (controladores) | `web/` (telas) e `rest/` (REST-API) |
| **Spring Data JPA** (entidades + DAOs) | `model/` (entidades `@Entity`) e `repository/` (interfaces `JpaRepository`) |
| **Thymeleaf** (visões) | `templates/` (catálogo, login, registro, carrinho, pedidos, áreas admin/loja, erro) |
| **REST-API disponibilizada** (entregável da **T7**) | `@RestController` em `rest/`: `/api/clientes`, `/api/lojas`, `/api/categorias`, `/api/produtos`, `/api/pedidos` |
| **App cliente da REST-API** (entregável da **T8**) | `client/` (RestClient) + `web/restclient/` + `templates/restclient/` → tela `/rest-client/categorias` |
| **Serviços** e demais artefatos | `service/`, além de `config/`, `dto/`, `exception/` |
| **Projeto Maven configurado** | `pom.xml` + wrapper `mvnw` / `mvnw.cmd` |
| **Roteiro de execução** (SGBD, banco, scripts, usuários, papéis) | seção [Roteiro de execução](#roteiro-de-execução-sgbd-banco-e-usuários) abaixo |
| **Entrega (URL do GitHub)** | <https://github.com/carlosamn-comp/loja-web> |

> Documentos de apoio no repositório: [`REQUISITOS.md`](REQUISITOS.md)/[`REQUISITOS.pdf`](REQUISITOS.pdf)
> (requisitos AA-1/AA-2), [`MAPEAMENTO-JPA.md`](MAPEAMENTO-JPA.md) (modelo e DDL) e
> [`api.http`](api.http) (exemplos da REST-API).

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

## Roteiro de execução (SGBD, banco e usuários)

- **SGBD:** **H2 Database** — banco relacional **embutido** (não precisa instalar nada
  separadamente; a dependência já vem no `pom.xml`). Roda em **modo arquivo**.
- **Nome do banco:** **`lojadb`** — URL JDBC `jdbc:h2:file:./data/lojadb`
  (arquivo `data/lojadb.mv.db`), usuário **`sa`**, senha **em branco**.
- **Scripts SQL:** **não é necessário rodar nenhum script manualmente.** O esquema
  (tabelas + chaves estrangeiras) é **criado/atualizado automaticamente** pelo Hibernate
  na inicialização (`spring.jpa.hibernate.ddl-auto=update`). O **DDL completo** gerado
  está documentado em [`MAPEAMENTO-JPA.md`](MAPEAMENTO-JPA.md).
- **Carga inicial (seed):** os dados de exemplo (usuários, categorias e produtos) são
  populados automaticamente por código em `config/DataSeeder.java` na primeira execução.
- **Console do banco (opcional):** acesse `http://localhost:8080/h2-console` e informe
  *JDBC URL* = `jdbc:h2:file:./data/lojadb`, *User Name* = `sa`, *Password* = (vazio).

### Usuários populados e papéis (todos com senha `123`)

| E-mail | Papel (role) | Origem | O que pode fazer |
|--------|--------------|--------|------------------|
| `admin@loja.com` | `ROLE_ADMIN` | banco (seed), tabela `usuario` `tipo=ADMIN`, senha BCrypt | CRUD de clientes, lojas e categorias; gerir o status de todos os pedidos |
| `loja@loja.com` (TechStore) | `ROLE_LOJA` | banco (seed) | cadastrar/editar os seus produtos; ver/atualizar as suas vendas |
| `loja2@loja.com` (Livraria Cultura) | `ROLE_LOJA` | banco (seed) | idem, para os seus produtos |
| `cliente@loja.com` (Maria Silva) | `ROLE_CLIENTE` | banco (seed) | comprar (carrinho/checkout) e ver os seus pedidos |

> Todos os usuários (admin, lojas e clientes) ficam na tabela `usuario` (herança JPA
> *single table*), com senha criptografada (BCrypt). Novos clientes podem se cadastrar em
> `/registro` e novas lojas em `/registro-loja`.

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
│   ├── carrinho/                # CarrinhoController (R5) — carrinho persistido no banco
│   └── restclient/              # T8: tela cliente que consome a REST-API (RestClient)
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

- **Herança JPA (Aula07):** `Usuario` é a classe-mãe abstrata e `Administrador`, `Loja`
  e `Cliente` são subclasses. Estratégia **`SINGLE_TABLE`** → tudo numa tabela `usuario`
  com a coluna discriminadora `tipo`. A role começa como **`"USER"`** (na classe-mãe) e é
  **renomeada** pelo construtor de cada subclasse para **`"ADMIN"`/`"LOJA"`/`"CLIENTE"`**,
  lida diretamente no login.
- **Autenticação:** todos os usuários ficam no **banco** (tabela `usuario`) com **senha
  criptografada (BCrypt)**. O `UsuarioDetailsService` resolve o login em **uma consulta**
  (`findByEmail`) e usa a `role` gravada. O admin é semeado no `DataSeeder`.
- **Carrinho persistente (no banco):** o carrinho do cliente é guardado na tabela
  `carrinho_item` (entidade `CarrinhoItem`, uma linha por cliente+produto), então **os
  itens permanecem ao fechar/reabrir a página** (e até entre logins).
- **Cliente REST (T8):** a tela `/rest-client/categorias` é uma aplicação que **consome a
  REST-API** via **`RestClient`** (em `client/CategoriaApiClient`), em vez de acessar o
  banco direto — demonstra um cliente da API.
- **Marketplace:** cada produto pertence a uma **loja**; um cliente compra de **várias
  lojas** no mesmo pedido. A loja gerencia só os seus produtos e vê só as suas vendas.
- **Imagens no banco:** armazenadas como bytes (BLOB) em `produto_imagem`, servidas por
  `GET /produtos/imagens/{id}` com o content-type correto (máx. 10 por produto). O produto
  "Mouse Gamer" já vem com uma imagem de exemplo (`resources/seed/mouse-gamer.jpg`).
- **Persistência:** H2 em arquivo (`data/lojadb.mv.db`). Para zerar, pare a app e apague
  a pasta `data/`.
- **CPF/CNPJ:** apenas obrigatórios e únicos (sem validação de formato — é um modelo).
- **Camadas (MVC):** `model` (entidades) → `repository` (DAOs) → `service` (regras) →
  `web`/`rest` (controllers) → `templates` (views). Veja os comentários no topo de cada
  classe para o papel dela.
