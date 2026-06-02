package com.exemplo.loja.web.loja;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.service.CategoriaService;
import com.exemplo.loja.service.LojaService;
import com.exemplo.loja.service.ProdutoService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Gestao de produtos pela LOJA logada (R3): a loja so ve e edita os seus
 * proprios produtos. Inclui upload de imagens (no maximo {@link ProdutoService#MAX_IMAGENS}).
 */
@Controller
@RequestMapping("/loja/produtos")
public class ProdutoLojaController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;
    private final LojaService lojaService;
    private final MessageSource messageSource;

    public ProdutoLojaController(ProdutoService produtoService, CategoriaService categoriaService,
                                 LojaService lojaService, MessageSource messageSource) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        this.lojaService = lojaService;
        this.messageSource = messageSource;
    }

    private Loja lojaLogada(Principal principal) {
        return lojaService.buscarPorEmail(principal.getName());
    }

    /** Garante que o produto pertence a loja logada (senao trata como inexistente). */
    private Produto produtoDaLoja(Long id, Loja loja) {
        Produto p = produtoService.buscar(id);
        if (p.getLoja() == null || !p.getLoja().getId().equals(loja.getId())) {
            throw new IllegalArgumentException("Produto nao encontrado: " + id);
        }
        return p;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        Loja loja = lojaLogada(principal);
        model.addAttribute("produtos", produtoService.listarPorLoja(loja.getId()));
        return "loja/produtos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("edicao", false);
        return "loja/produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Principal principal, Model model) {
        Loja loja = lojaLogada(principal);
        model.addAttribute("produto", produtoDaLoja(id, loja));
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("imagemIds", produtoService.listarImagemIds(id));
        model.addAttribute("edicao", true);
        return "loja/produtos/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("produto") Produto produto,
                        BindingResult result,
                        @RequestParam(required = false) Long categoriaId,
                        @RequestParam(value = "arquivos", required = false) MultipartFile[] arquivos,
                        Principal principal, Model model) {
        if (categoriaId == null) {
            result.rejectValue("categoria", "produto.categoria.obrigatoria");
        }
        List<String> errosImagem = validarImagens(arquivos, 0);
        if (result.hasErrors() || !errosImagem.isEmpty()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("errosImagem", errosImagem);
            model.addAttribute("edicao", false);
            return "loja/produtos/form";
        }
        produtoService.salvar(produto, categoriaId, lojaLogada(principal), arquivos);
        return "redirect:/loja/produtos";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("produto") Produto produto,
                            BindingResult result,
                            @RequestParam(required = false) Long categoriaId,
                            @RequestParam(value = "arquivos", required = false) MultipartFile[] arquivos,
                            Principal principal, Model model) {
        Loja loja = lojaLogada(principal);
        produtoDaLoja(id, loja); // valida posse
        if (categoriaId == null) {
            result.rejectValue("categoria", "produto.categoria.obrigatoria");
        }
        List<String> errosImagem = validarImagens(arquivos, produtoService.contarImagens(id));
        if (result.hasErrors() || !errosImagem.isEmpty()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("imagemIds", produtoService.listarImagemIds(id));
            model.addAttribute("errosImagem", errosImagem);
            model.addAttribute("edicao", true);
            return "loja/produtos/form";
        }
        produtoService.atualizar(id, produto, categoriaId, arquivos);
        return "redirect:/loja/produtos/" + id + "/editar";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, Principal principal) {
        produtoDaLoja(id, lojaLogada(principal));
        produtoService.excluir(id);
        return "redirect:/loja/produtos";
    }

    @PostMapping("/{produtoId}/imagens/{imagemId}/excluir")
    public String excluirImagem(@PathVariable Long produtoId, @PathVariable Long imagemId,
                                Principal principal) {
        produtoDaLoja(produtoId, lojaLogada(principal));
        produtoService.excluirImagem(imagemId);
        return "redirect:/loja/produtos/" + produtoId + "/editar";
    }

    private List<String> validarImagens(MultipartFile[] arquivos, long existentes) {
        List<String> erros = new ArrayList<>();
        if (arquivos == null) {
            return erros;
        }
        int novos = 0;
        boolean tipoInvalido = false;
        for (MultipartFile a : arquivos) {
            if (a == null || a.isEmpty()) {
                continue;
            }
            novos++;
            String tipo = a.getContentType();
            if (tipo == null || !tipo.startsWith("image/")) {
                tipoInvalido = true;
            }
        }
        if (tipoInvalido) {
            erros.add(msg("produto.imagens.tipo"));
        }
        if (existentes + novos > ProdutoService.MAX_IMAGENS) {
            erros.add(msg("produto.imagens.limite"));
        }
        return erros;
    }

    private String msg(String code) {
        return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }
}
