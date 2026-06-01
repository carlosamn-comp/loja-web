package com.exemplo.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Imagem de um produto, armazenada no proprio banco de dados como bytes (BLOB),
 * junto com o seu content-type (ex.: "image/png"). Cada produto pode ter no
 * maximo 10 imagens (regra aplicada no {@code ProdutoService}).
 */
@Entity
@Table(name = "produto_imagem")
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Conteudo binario da imagem (BLOB). Nao e exposto em JSON. */
    @Lob
    @JsonIgnore
    @Column(name = "dados", nullable = false)
    private byte[] dados;

    /** Tipo MIME da imagem (ex.: image/png, image/jpeg). */
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "nome_arquivo", length = 255)
    private String nomeArquivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnore
    private Produto produto;

    public ProdutoImagem() {
    }

    public ProdutoImagem(byte[] dados, String contentType, String nomeArquivo, Produto produto) {
        this.dados = dados;
        this.contentType = contentType;
        this.nomeArquivo = nomeArquivo;
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
