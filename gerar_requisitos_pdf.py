# -*- coding: utf-8 -*-
"""Gera o PDF do documento de requisitos da Loja Web (T5/AA-1/AA-2)."""

from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.lib.enums import TA_JUSTIFY
from reportlab.lib.colors import HexColor
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, HRFlowable, ListFlowable,
    ListItem, Preformatted, PageBreak,
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

import os
OUTPUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "REQUISITOS.pdf")

DARK = HexColor("#1a1a1a")
GREY = HexColor("#555555")
CODE_BG = HexColor("#f4f4f4")
LINK = HexColor("#1155cc")

styles = getSampleStyleSheet()

h_title = ParagraphStyle(
    "HTitle", parent=styles["Title"], fontName="Helvetica-Bold",
    fontSize=20, leading=24, textColor=DARK, spaceAfter=4,
)
h_sub = ParagraphStyle(
    "HSub", parent=styles["Normal"], fontName="Helvetica-Bold",
    fontSize=10, leading=14, textColor=GREY, spaceAfter=2,
)
h2 = ParagraphStyle(
    "H2", parent=styles["Heading2"], fontName="Helvetica-Bold",
    fontSize=14, leading=18, textColor=DARK, spaceBefore=14, spaceAfter=8,
)
h3 = ParagraphStyle(
    "H3", parent=styles["Heading3"], fontName="Helvetica-Bold",
    fontSize=11, leading=15, textColor=DARK, spaceBefore=10, spaceAfter=4,
)
body = ParagraphStyle(
    "Body", parent=styles["Normal"], fontName="Helvetica",
    fontSize=10, leading=15, alignment=TA_JUSTIFY, spaceAfter=6,
)
bullet = ParagraphStyle(
    "Bullet", parent=body, spaceAfter=6, leading=15,
)
note = ParagraphStyle(
    "Note", parent=body, textColor=GREY, fontSize=9, leading=13,
)
code = ParagraphStyle(
    "Code", parent=styles["Code"], fontName="Courier", fontSize=7.5,
    leading=10, backColor=CODE_BG, borderPadding=6, textColor=DARK,
)
footnote = ParagraphStyle(
    "Footnote", parent=body, fontSize=8, leading=11, textColor=GREY,
    alignment=0,
)


def hr():
    return HRFlowable(width="100%", thickness=0.8, color=HexColor("#cccccc"),
                      spaceBefore=8, spaceAfter=8)


def bullets(items):
    return ListFlowable(
        [ListItem(Paragraph(t, bullet), leftIndent=10, value="•") for t in items],
        bulletType="bullet", bulletColor=DARK, leftIndent=14, bulletFontSize=8,
    )


story = []

# ---------- Cabecalho ----------
story.append(Paragraph("DESENVOLVIMENTO DE SOFTWARE PARA A WEB 1", h_title))
story.append(Paragraph("Sistema Web: Loja / E-commerce de Produtos", h_sub))
story.append(Paragraph("Arquitetura: Modelo-Visão-Controlador (MVC)", h_sub))
story.append(hr())

# ---------- AA-1 ----------
story.append(Paragraph("Atividade AA-1: Sistema para uma loja virtual (e-commerce)", h2))

story.append(Paragraph(
    "O sistema deve possuir um cadastro de <b>clientes</b>, com os seguintes dados: "
    "e-mail, senha, CPF, nome, telefone, sexo e data de nascimento.", body))
story.append(Paragraph(
    "O sistema deve possuir um <b>administrador</b> (operador da loja), responsável pela "
    "gestão do catálogo e dos pedidos, com os seguintes dados de acesso: e-mail (ou nome de "
    "usuário) e senha.<super>2</super>", body))
story.append(Paragraph(
    "O catálogo da loja é organizado em <b>categorias</b>, e cada <b>produto</b> pertence a "
    "uma categoria. As compras dos clientes são registradas como <b>pedidos</b>, e cada pedido "
    "é composto por um ou mais <b>itens de pedido</b> (produto + quantidade).", body))
story.append(Paragraph("O sistema deve atender aos seguintes requisitos:", body))

story.append(bullets([
    "<b>R1:</b> CRUD<super>1</super> de <b>clientes</b> (requer login de administrador).",
    "<b>R2:</b> CRUD de <b>categorias</b> de produtos (requer login de administrador). "
    "Os dados de uma categoria são: nome e (opcionalmente) descrição.",
    "<b>R3:</b> CRUD de <b>produtos</b> (requer login de administrador). Depois de fazer login, "
    "o administrador pode cadastrar, editar e remover produtos do catálogo. O cadastro de "
    "produtos deve possuir os seguintes dados: nome, descrição, preço, quantidade em estoque, "
    "categoria à qual pertence e fotos (no máximo 10 imagens) do produto.",
    "<b>R4:</b> Listagem de todos os produtos em uma única página (não requer login). O sistema "
    "deve prover a funcionalidade de <b>filtrar</b> os produtos por nome, por categoria ou por "
    "faixa de preço.",
    "<b>R5:</b> Realização de <b>pedido de compra</b> (requer login do cliente via e-mail + senha). "
    "Ao navegar pelo catálogo (requisito R4), o cliente pode adicionar produtos ao seu pedido "
    "(carrinho), informando a quantidade de cada item, e finalizar a compra. A data atual (quando "
    "o pedido foi realizado) deve ser registrada no sistema, e o pedido nasce com o status ABERTO. "
    "O cliente deve ser informado (via e-mail) sobre a confirmação do pedido, com o resumo dos "
    "itens e o valor total.",
    "<b>R6:</b> Listagem de todos os <b>pedidos de um cliente</b> (requer login do cliente via "
    "e-mail + senha). Depois de fazer login, o cliente pode visualizar todos os seus pedidos com "
    "os respectivos <i>status</i> e o valor total de cada um. <b>ABERTO</b> indica que o pedido foi "
    "realizado e aguarda processamento. <b>PAGO</b> indica que o pagamento foi confirmado. "
    "<b>ENVIADO</b> indica que o pedido foi despachado. <b>CANCELADO</b> indica que o pedido foi "
    "cancelado.",
    "<b>R7:</b> Gestão de <b>pedidos</b> pelo administrador (requer login de administrador). Depois "
    "de fazer login, o administrador pode visualizar todos os pedidos realizados na loja e "
    "<b>atualizar o status</b> de cada um (ABERTO → PAGO → ENVIADO, ou CANCELADO). A cada mudança "
    "de status, o cliente deve ser informado (via e-mail). O sistema deve também prover a "
    "funcionalidade de filtrar os pedidos por status.",
    "<b>R8:</b> O sistema deve ser <b>internacionalizado</b> em pelo menos dois idiomas: "
    "português + outro de sua escolha.",
    "<b>R9:</b> O sistema deve <b>validar</b> (tamanho, formato, etc.) todas as informações "
    "(campos nos formulários) cadastradas e/ou editadas.",
]))

story.append(Paragraph(
    "O sistema deve <b>tratar todos os erros possíveis</b> (cadastros duplicados, problemas "
    "técnicos, etc.) mostrando uma página de erros amigável ao usuário e registrando o erro no "
    "console.", body))

story.append(Paragraph("Arquitetura", h3))
story.append(Paragraph("Modelo-Visão-Controlador (MVC).", body))
story.append(Paragraph("Tecnologias", h3))
story.append(bullets([
    "Spring MVC, Spring Data JPA, Spring Security &amp; Thymeleaf (Lado Servidor)",
    "JavaScript &amp; CSS (Lado Cliente)",
]))
story.append(Paragraph("Ambiente de Desenvolvimento", h3))
story.append(bullets([
    "A compilação e o <i>deployment</i> devem ser obrigatoriamente realizados via <b>Maven</b>.",
    "Os arquivos-fonte do sistema devem estar hospedados obrigatoriamente em um repositório "
    "(preferencialmente <b>GitHub</b>).",
]))

story.append(hr())
story.append(Paragraph(
    "1. CRUD: Create, Read, Update &amp; Delete.", footnote))
story.append(Paragraph(
    "2. O login + senha do administrador devem ser populados no banco de dados durante a "
    "inicialização do sistema.", footnote))

story.append(PageBreak())

# ---------- AA-2 ----------
story.append(Paragraph("DESENVOLVIMENTO DE SOFTWARE PARA A WEB 1", h_title))
story.append(Paragraph("Sistema Web: Loja / E-commerce de Produtos", h_sub))
story.append(hr())
story.append(Paragraph(
    "Atividade AA-2: Sistema para uma loja virtual (e-commerce) — REST API", h2))

story.append(Paragraph(
    "<b>Obs 1:</b> Esta atividade deve ser baseada na atividade AA-1. Ou seja, deve-se apenas "
    "implementar os novos requisitos (funcionalidades providas em uma REST API) aqui mencionados "
    "— levando em consideração o que já foi desenvolvido na atividade AA-1.", body))
story.append(Paragraph("O sistema deve incorporar os seguintes requisitos.", body))


def endpoint_block(title, lines):
    story.append(Paragraph(title, h3))
    items = []
    for label, method_url, has_body in lines:
        txt = f"<b>{label}</b><br/><font face='Courier' size=8 color='#1155cc'>{method_url}</font>"
        if has_body:
            txt += "<br/><font size=9 color='#555555'>Body: raw/JSON (application/json)</font>"
        items.append(txt)
    story.append(bullets(items))


endpoint_block("REST API — CRUD de clientes", [
    ("Cria um novo cliente [Create - CRUD]", "POST http://localhost:8080/api/clientes", True),
    ("Retorna a lista de clientes [Read - CRUD]", "GET http://localhost:8080/api/clientes", False),
    ("Retorna o cliente de id = {id} [Read - CRUD]", "GET http://localhost:8080/api/clientes/{id}", False),
    ("Atualiza o cliente de id = {id} [Update - CRUD]", "PUT http://localhost:8080/api/clientes/{id}", True),
    ("Remove o cliente de id = {id} [Delete - CRUD]", "DELETE http://localhost:8080/api/clientes/{id}", False),
])

endpoint_block("REST API — CRUD de categorias", [
    ("Cria uma nova categoria [Create - CRUD]", "POST http://localhost:8080/api/categorias", True),
    ("Retorna a lista de categorias [Read - CRUD]", "GET http://localhost:8080/api/categorias", False),
    ("Retorna a categoria de id = {id} [Read - CRUD]", "GET http://localhost:8080/api/categorias/{id}", False),
    ("Atualiza a categoria de id = {id} [Update - CRUD]", "PUT http://localhost:8080/api/categorias/{id}", True),
    ("Remove a categoria de id = {id} [Delete - CRUD]", "DELETE http://localhost:8080/api/categorias/{id}", False),
])

endpoint_block("REST API — CRUD de produtos", [
    ("Cria um novo produto [Create - CRUD]", "POST http://localhost:8080/api/produtos", True),
    ("Retorna a lista de produtos [Read - CRUD]", "GET http://localhost:8080/api/produtos", False),
    ("Retorna o produto de id = {id} [Read - CRUD]", "GET http://localhost:8080/api/produtos/{id}", False),
    ("Atualiza o produto de id = {id} [Update - CRUD]", "PUT http://localhost:8080/api/produtos/{id}", True),
    ("Remove o produto de id = {id} [Delete - CRUD]", "DELETE http://localhost:8080/api/produtos/{id}", False),
])

endpoint_block("REST API — Consultas adicionais", [
    ("Retorna a lista de produtos cujo nome contém {nome} [Read - CRUD]", "GET http://localhost:8080/api/produtos?nome={nome}", False),
    ("Retorna a lista de produtos da categoria de id = {id} [Read - CRUD]", "GET http://localhost:8080/api/produtos/categorias/{id}", False),
    ("Cria um novo pedido para o cliente de id = {id} [Create - CRUD]", "POST http://localhost:8080/api/pedidos/clientes/{id}", True),
    ("Retorna a lista de pedidos do cliente de id = {id} [Read - CRUD]", "GET http://localhost:8080/api/pedidos/clientes/{id}", False),
    ("Retorna a lista de pedidos por status = {status} [Read - CRUD]", "GET http://localhost:8080/api/pedidos/status/{status}", False),
])

story.append(Paragraph(
    "<b>Obs 2:</b> Em todas as funcionalidades da REST API mencionadas acima, não há necessidade "
    "de autenticação (login).", body))
story.append(Paragraph(
    "<b>Dica:</b> Na configuração do Spring Security, utilize algo semelhante ao apresentado no "
    "código abaixo (liberando os endpoints <font face='Courier'>/api/**</font> e exigindo login "
    "nas demais rotas Web):", body))

sec_code = """@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests((authz) -> authz
            // Endpoints da REST API (publicos)
            .requestMatchers("/api/**").permitAll()
            // Paginas publicas (catalogo, login, recursos estaticos)
            .requestMatchers("/", "/produtos", "/login", "/css/**", "/js/**").permitAll()
            // Rotas de administracao exigem o papel ADMIN
            .requestMatchers("/admin/**").hasRole("ADMIN")
            // Demais rotas exigem usuario autenticado (cliente logado)
            .anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin((form) -> form
            .loginPage("/login")
            .permitAll())
        .logout((logout) -> logout
            .logoutSuccessUrl("/").permitAll());
    return http.build();
}"""
story.append(Preformatted(sec_code, code))

story.append(Paragraph("Arquitetura", h3))
story.append(Paragraph("Modelo-Visão-Controlador (MVC).", body))
story.append(Paragraph("Tecnologias", h3))
story.append(bullets([
    "Spring MVC (Controladores REST), Spring Data JPA, Spring Security &amp; Thymeleaf (Lado Servidor)",
]))
story.append(Paragraph("Ambiente de Desenvolvimento", h3))
story.append(bullets([
    "A compilação e o <i>deployment</i> devem ser obrigatoriamente realizados via <b>Maven</b>.",
    "Os arquivos-fonte do sistema devem estar hospedados obrigatoriamente em um repositório "
    "(preferencialmente <b>GitHub</b>).",
]))
story.append(hr())
story.append(Paragraph("1. CRUD: Create, Read, Update &amp; Delete.", footnote))


doc = SimpleDocTemplate(
    OUTPUT, pagesize=A4,
    leftMargin=2.2 * cm, rightMargin=2.2 * cm,
    topMargin=2 * cm, bottomMargin=2 * cm,
    title="Requisitos - Loja Web (AA-1 / AA-2)",
    author="Carlos M.",
)
doc.build(story)
print("PDF gerado:", OUTPUT)
