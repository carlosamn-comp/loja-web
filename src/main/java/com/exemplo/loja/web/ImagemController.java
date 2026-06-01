package com.exemplo.loja.web;

import com.exemplo.loja.model.ProdutoImagem;
import com.exemplo.loja.service.ProdutoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Serve as imagens de produto armazenadas no banco, devolvendo os bytes com o
 * content-type correto (ex.: image/png) para uso direto em &lt;img src&gt;.
 *
 * A rota fica sob /produtos/** (publica), permitindo exibir as imagens no
 * catalogo sem autenticacao.
 */
@Controller
public class ImagemController {

    private final ProdutoService produtoService;

    public ImagemController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/produtos/imagens/{id}")
    public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        ProdutoImagem img = produtoService.buscarImagem(id);
        MediaType tipo = img.getContentType() != null
                ? MediaType.parseMediaType(img.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok().contentType(tipo).body(img.getDados());
    }
}
