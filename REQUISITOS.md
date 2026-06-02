# DESENVOLVIMENTO DE SOFTWARE PARA A WEB 1

**Sistema Web:** Marketplace de Produtos (lojas/vendedores + clientes)
**Repositório:** https://github.com/carlosamn-comp/loja-web
**Arquitetura:** Modelo-Visão-Controlador (MVC)

> Documento de requisitos do sistema Web escolhido, baseado na estrutura das
> atividades AA-1 e AA-2 da disciplina. O sistema é um **marketplace** (semelhante
> ao Mercado Livre): várias **lojas/vendedores** disponibilizam produtos e os
> **clientes** compram produtos de diferentes lojas. O **administrador** gerencia
> clientes e lojas.

---

## Atividade AA-1: Marketplace de produtos

O sistema deve possuir um cadastro de **clientes**, com os seguintes dados:
e-mail, senha, CPF, nome, telefone, sexo e data de nascimento.

O sistema deve possuir um cadastro de **lojas/vendedores**, com os seguintes dados:
e-mail, senha, CNPJ, nome e descrição.

O sistema deve possuir um **administrador** (operador da plataforma), responsável
pela gestão de clientes e lojas, cujo login + senha são populados no banco de dados
durante a inicialização do sistema.[^2]

Os produtos são organizados em **categorias** e **cada produto pertence a uma loja**.
Um **cliente pode comprar produtos de diferentes lojas** num mesmo pedido; cada
pedido é composto por um ou mais **itens** (produto + quantidade).

O sistema deve atender aos seguintes requisitos:

- **R1:** CRUD[^1] de **clientes** (requer login de administrador).

- **R2:** CRUD de **lojas/vendedores** (requer login de administrador). Além disso,
  uma loja pode se **auto-cadastrar** publicamente (`/registro-loja`) para começar a
  vender.

- **R3:** Cadastro de **produtos para venda** (requer login da loja via e-mail +
  senha). Depois de fazer login, a loja pode cadastrar, editar e remover **os seus
  próprios** produtos. O cadastro de produtos deve possuir: nome, descrição, preço,
  quantidade em estoque, categoria e fotos (no máximo 10 imagens) do produto.

- **R4:** Listagem de todos os produtos em uma única página (não requer login). O
  sistema deve prover a funcionalidade de **filtrar** os produtos por nome, por
  **loja**, por categoria ou por faixa de preço.

- **R5:** Realização de **pedido de compra** (requer login do cliente via e-mail +
  senha). Navegando pelo catálogo (R4), o cliente adiciona ao carrinho produtos de
  **uma ou mais lojas** e finaliza a compra. A data do pedido é registrada e o pedido
  nasce com status **ABERTO**. O cliente é informado (via e-mail) sobre a confirmação.

- **R6:** Listagem de todos os **pedidos de um cliente** (requer login do cliente).
  Depois de fazer login, o cliente visualiza todos os seus pedidos com os respectivos
  **status** (ABERTO, PAGO, ENVIADO, CANCELADO) e o valor total.

- **R7:** Gestão das **vendas pela loja** (requer login da loja). Depois de fazer
  login, a loja visualiza os pedidos que contêm os seus produtos e pode **atualizar o
  status** (ABERTO → PAGO → ENVIADO, ou CANCELADO), notificando o cliente por e-mail.
  O administrador também pode visualizar e atualizar o status de todos os pedidos.

- **R8:** O sistema deve ser **internacionalizado** em pelo menos dois idiomas:
  português + outro de sua escolha (inglês).

- **R9:** O sistema deve **validar** (tamanho, formato, etc.) todas as informações
  (campos nos formulários) cadastradas e/ou editadas.

O sistema deve **tratar todos os erros possíveis** (cadastros duplicados, problemas
técnicos, etc.) mostrando uma página de erros amigável ao usuário e registrando o
erro no console.

### Arquitetura
Modelo-Visão-Controlador (MVC).

### Tecnologias
- Spring MVC, Spring Data JPA, Spring Security & Thymeleaf (Lado Servidor)
- JavaScript & CSS (Lado Cliente)

### Ambiente de Desenvolvimento
- A compilação e o *deployment* devem ser obrigatoriamente realizados via **Maven**.
- Os arquivos-fonte devem estar hospedados obrigatoriamente em um repositório (**GitHub**).

[^1]: CRUD: **C**reate, **R**ead, **U**pdate & **D**elete.
[^2]: O login + senha do administrador são populados no banco de dados durante a inicialização do sistema.

---

## Atividade AA-2: Marketplace de produtos — REST API

**Obs 1:** Esta atividade é baseada na AA-1; implementam-se apenas os novos
requisitos (funcionalidades providas em uma REST API).

### REST API — CRUD de clientes
- **Cria** [Create] `POST http://localhost:8080/api/clientes` — Body: raw/JSON
- **Lista** [Read] `GET http://localhost:8080/api/clientes`
- **Por id** [Read] `GET http://localhost:8080/api/clientes/{id}`
- **Atualiza** [Update] `PUT http://localhost:8080/api/clientes/{id}` — Body: raw/JSON
- **Remove** [Delete] `DELETE http://localhost:8080/api/clientes/{id}`

### REST API — CRUD de lojas
- **Cria** [Create] `POST http://localhost:8080/api/lojas` — Body: raw/JSON
- **Lista** [Read] `GET http://localhost:8080/api/lojas`
- **Por id** [Read] `GET http://localhost:8080/api/lojas/{id}`
- **Atualiza** [Update] `PUT http://localhost:8080/api/lojas/{id}` — Body: raw/JSON
- **Remove** [Delete] `DELETE http://localhost:8080/api/lojas/{id}`

### REST API — CRUD de produtos
- **Cria** [Create] `POST http://localhost:8080/api/produtos` — Body: raw/JSON
  (informe `categoria` e `loja` pelo id: `{"categoria":{"id":1},"loja":{"id":1}}`)
- **Lista** [Read] `GET http://localhost:8080/api/produtos`
- **Por id** [Read] `GET http://localhost:8080/api/produtos/{id}`
- **Atualiza** [Update] `PUT http://localhost:8080/api/produtos/{id}` — Body: raw/JSON
- **Remove** [Delete] `DELETE http://localhost:8080/api/produtos/{id}`

### REST API — Consultas adicionais
- **Produtos cujo nome contém {nome}** `GET http://localhost:8080/api/produtos?nome={nome}`
- **Produtos da categoria {id}** `GET http://localhost:8080/api/produtos/categorias/{id}`
- **Produtos da loja {id}** `GET http://localhost:8080/api/produtos/lojas/{id}`
- **Cria pedido para o cliente {id}** `POST http://localhost:8080/api/pedidos/clientes/{id}` — Body: raw/JSON
- **Pedidos do cliente {id}** `GET http://localhost:8080/api/pedidos/clientes/{id}`
- **Pedidos por status {status}** `GET http://localhost:8080/api/pedidos/status/{status}`

**Obs 2:** Em todas as funcionalidades da REST API, não há necessidade de autenticação.

### Arquitetura / Tecnologias / Ambiente
Modelo-Visão-Controlador; Spring MVC (Controladores REST), Spring Data JPA, Spring
Security & Thymeleaf; compilação e *deployment* via **Maven**; código no **GitHub**.

[^1]: CRUD: **C**reate, **R**ead, **U**pdate & **D**elete.
