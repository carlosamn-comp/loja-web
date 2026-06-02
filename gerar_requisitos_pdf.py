# -*- coding: utf-8 -*-
"""Gera o PDF do documento de requisitos do Marketplace (AA-1/AA-2)."""

import os
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm
from reportlab.lib.enums import TA_JUSTIFY
from reportlab.lib.colors import HexColor
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, HRFlowable, ListFlowable,
    ListItem, Preformatted, PageBreak,
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

OUTPUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "REQUISITOS.pdf")

DARK = HexColor("#1a1a1a")
GREY = HexColor("#555555")
CODE_BG = HexColor("#f4f4f4")

styles = getSampleStyleSheet()
h_title = ParagraphStyle("HTitle", parent=styles["Title"], fontName="Helvetica-Bold",
                         fontSize=20, leading=24, textColor=DARK, spaceAfter=4)
h_sub = ParagraphStyle("HSub", parent=styles["Normal"], fontName="Helvetica-Bold",
                       fontSize=10, leading=14, textColor=GREY, spaceAfter=2)
h2 = ParagraphStyle("H2", parent=styles["Heading2"], fontName="Helvetica-Bold",
                    fontSize=14, leading=18, textColor=DARK, spaceBefore=14, spaceAfter=8)
h3 = ParagraphStyle("H3", parent=styles["Heading3"], fontName="Helvetica-Bold",
                    fontSize=11, leading=15, textColor=DARK, spaceBefore=10, spaceAfter=4)
body = ParagraphStyle("Body", parent=styles["Normal"], fontName="Helvetica",
                      fontSize=10, leading=15, alignment=TA_JUSTIFY, spaceAfter=6)
bullet = ParagraphStyle("Bullet", parent=body, spaceAfter=6, leading=15)
footnote = ParagraphStyle("Footnote", parent=body, fontSize=8, leading=11, textColor=GREY, alignment=0)


def hr():
    return HRFlowable(width="100%", thickness=0.8, color=HexColor("#cccccc"),
                      spaceBefore=8, spaceAfter=8)


def bullets(items):
    return ListFlowable(
        [ListItem(Paragraph(t, bullet), leftIndent=10, value="•") for t in items],
        bulletType="bullet", bulletColor=DARK, leftIndent=14, bulletFontSize=8)


story = []

# ---------- Cabecalho ----------
story.append(Paragraph("DESENVOLVIMENTO DE SOFTWARE PARA A WEB 1", h_title))
story.append(Paragraph("Sistema Web: Marketplace de Produtos (lojas/vendedores + clientes)", h_sub))
story.append(Paragraph("Arquitetura: Modelo-Visão-Controlador (MVC)", h_sub))
story.append(hr())

# ---------- AA-1 ----------
story.append(Paragraph("Atividade AA-1: Marketplace de produtos", h2))
story.append(Paragraph(
    "O sistema deve possuir um cadastro de <b>clientes</b>, com os seguintes dados: e-mail, "
    "senha, CPF, nome, telefone, sexo e data de nascimento.", body))
story.append(Paragraph(
    "O sistema deve possuir um cadastro de <b>lojas/vendedores</b>, com os seguintes dados: "
    "e-mail, senha, CNPJ, nome e descrição.", body))
story.append(Paragraph(
    "O sistema deve possuir um <b>administrador</b> (operador da plataforma), responsável pela "
    "gestão de clientes e lojas, cujo login + senha são populados no banco na inicialização.<super>2</super>", body))
story.append(Paragraph(
    "Os produtos são organizados em <b>categorias</b> e <b>cada produto pertence a uma loja</b>. "
    "Um <b>cliente pode comprar produtos de diferentes lojas</b> num mesmo pedido; cada pedido é "
    "composto por um ou mais itens (produto + quantidade).", body))
story.append(Paragraph("O sistema deve atender aos seguintes requisitos:", body))

story.append(bullets([
    "<b>R1:</b> CRUD<super>1</super> de <b>clientes</b> (requer login de administrador).",
    "<b>R2:</b> CRUD de <b>lojas/vendedores</b> (requer login de administrador). Além disso, uma "
    "loja pode se <b>auto-cadastrar</b> publicamente para começar a vender.",
    "<b>R3:</b> Cadastro de <b>produtos para venda</b> (requer login da loja). Depois de fazer login, "
    "a loja pode cadastrar, editar e remover <b>os seus próprios</b> produtos. Dados do produto: nome, "
    "descrição, preço, quantidade em estoque, categoria e fotos (no máximo 10 imagens).",
    "<b>R4:</b> Listagem de todos os produtos em uma única página (não requer login), com filtro por "
    "nome, por <b>loja</b>, por categoria ou por faixa de preço.",
    "<b>R5:</b> Realização de <b>pedido de compra</b> (requer login do cliente). O cliente adiciona ao "
    "carrinho produtos de <b>uma ou mais lojas</b> e finaliza a compra; a data é registrada, o pedido "
    "nasce ABERTO e o cliente é informado por e-mail.",
    "<b>R6:</b> Listagem de todos os <b>pedidos de um cliente</b> (requer login do cliente), com os "
    "status (ABERTO, PAGO, ENVIADO, CANCELADO) e o valor total.",
    "<b>R7:</b> Gestão das <b>vendas pela loja</b> (requer login da loja): a loja visualiza os pedidos "
    "que contêm os seus produtos e atualiza o status, notificando o cliente por e-mail. O administrador "
    "também visualiza e atualiza o status de todos os pedidos.",
    "<b>R8:</b> O sistema deve ser <b>internacionalizado</b> em pelo menos dois idiomas: português + inglês.",
    "<b>R9:</b> O sistema deve <b>validar</b> todas as informações (campos nos formulários) cadastradas/editadas.",
]))
story.append(Paragraph(
    "O sistema deve <b>tratar todos os erros possíveis</b> (cadastros duplicados, problemas técnicos, "
    "etc.) mostrando uma página de erros amigável ao usuário e registrando o erro no console.", body))

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
    "Os arquivos-fonte devem estar hospedados obrigatoriamente em um repositório (<b>GitHub</b>).",
]))
story.append(hr())
story.append(Paragraph("1. CRUD: Create, Read, Update &amp; Delete.", footnote))
story.append(Paragraph("2. O login + senha do administrador são populados no banco durante a inicialização do sistema.", footnote))

story.append(PageBreak())

# ---------- AA-2 ----------
story.append(Paragraph("DESENVOLVIMENTO DE SOFTWARE PARA A WEB 1", h_title))
story.append(Paragraph("Sistema Web: Marketplace de Produtos", h_sub))
story.append(hr())
story.append(Paragraph("Atividade AA-2: Marketplace de produtos — REST API", h2))
story.append(Paragraph(
    "<b>Obs 1:</b> Esta atividade é baseada na AA-1; implementam-se apenas os novos requisitos "
    "(funcionalidades providas em uma REST API).", body))


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
    ("Cria um novo cliente [Create]", "POST http://localhost:8080/api/clientes", True),
    ("Lista os clientes [Read]", "GET http://localhost:8080/api/clientes", False),
    ("Cliente de id = {id} [Read]", "GET http://localhost:8080/api/clientes/{id}", False),
    ("Atualiza o cliente {id} [Update]", "PUT http://localhost:8080/api/clientes/{id}", True),
    ("Remove o cliente {id} [Delete]", "DELETE http://localhost:8080/api/clientes/{id}", False),
])
endpoint_block("REST API — CRUD de lojas", [
    ("Cria uma nova loja [Create]", "POST http://localhost:8080/api/lojas", True),
    ("Lista as lojas [Read]", "GET http://localhost:8080/api/lojas", False),
    ("Loja de id = {id} [Read]", "GET http://localhost:8080/api/lojas/{id}", False),
    ("Atualiza a loja {id} [Update]", "PUT http://localhost:8080/api/lojas/{id}", True),
    ("Remove a loja {id} [Delete]", "DELETE http://localhost:8080/api/lojas/{id}", False),
])
endpoint_block("REST API — CRUD de produtos (informe categoria e loja pelo id)", [
    ("Cria um novo produto [Create]", "POST http://localhost:8080/api/produtos", True),
    ("Lista os produtos [Read]", "GET http://localhost:8080/api/produtos", False),
    ("Produto de id = {id} [Read]", "GET http://localhost:8080/api/produtos/{id}", False),
    ("Atualiza o produto {id} [Update]", "PUT http://localhost:8080/api/produtos/{id}", True),
    ("Remove o produto {id} [Delete]", "DELETE http://localhost:8080/api/produtos/{id}", False),
])
endpoint_block("REST API — Consultas adicionais", [
    ("Produtos cujo nome contém {nome} [Read]", "GET http://localhost:8080/api/produtos?nome={nome}", False),
    ("Produtos da categoria {id} [Read]", "GET http://localhost:8080/api/produtos/categorias/{id}", False),
    ("Produtos da loja {id} [Read]", "GET http://localhost:8080/api/produtos/lojas/{id}", False),
    ("Cria pedido para o cliente {id} [Create]", "POST http://localhost:8080/api/pedidos/clientes/{id}", True),
    ("Pedidos do cliente {id} [Read]", "GET http://localhost:8080/api/pedidos/clientes/{id}", False),
    ("Pedidos por status {status} [Read]", "GET http://localhost:8080/api/pedidos/status/{status}", False),
])

story.append(Paragraph(
    "<b>Obs 2:</b> Em todas as funcionalidades da REST API, não há necessidade de autenticação.", body))

ex = """// Exemplo de corpo para criar um produto (POST /api/produtos)
{
  "nome": "Mouse Gamer",
  "descricao": "RGB",
  "preco": 150.0,
  "estoque": 20,
  "categoria": { "id": 3 },
  "loja": { "id": 1 }
}"""
story.append(Preformatted(ex, ParagraphStyle("Code", parent=styles["Code"], fontName="Courier",
             fontSize=8, leading=11, backColor=CODE_BG, borderPadding=6, textColor=DARK)))

story.append(Paragraph("Arquitetura / Tecnologias / Ambiente", h3))
story.append(Paragraph(
    "Modelo-Visão-Controlador; Spring MVC (Controladores REST), Spring Data JPA, Spring Security &amp; "
    "Thymeleaf; compilação e <i>deployment</i> via <b>Maven</b>; código no <b>GitHub</b>.", body))
story.append(hr())
story.append(Paragraph("1. CRUD: Create, Read, Update &amp; Delete.", footnote))


doc = SimpleDocTemplate(OUTPUT, pagesize=A4, leftMargin=2.2 * cm, rightMargin=2.2 * cm,
                        topMargin=2 * cm, bottomMargin=2 * cm,
                        title="Requisitos - Marketplace (AA-1 / AA-2)", author="Carlos M.")
doc.build(story)
print("PDF gerado:", OUTPUT)
