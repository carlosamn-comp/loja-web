package com.exemplo.loja.web.admin;

import com.exemplo.loja.model.Produto;
import com.exemplo.loja.service.CategoriaService;
import com.exemplo.loja.service.ProdutoService;
import jakarta.validation.Valid;
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
 * CRUD administrativo de produtos (R3), incluindo upload de imagens (no maximo
 * {@link ProdutoService#MAX_IMAGENS}). Requer perfil ADMIN.
 */
@Controller
@RequestMapping("/admin/produtos")
public class ProdutoAdminController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    public ProdutoAdminController(ProdutoService produtoService, CategoriaService categoriaService,
                                  MessageSource messageSource) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listar());
        return "admin/produtos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("edicao", false);
        return "admin/produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", produtoService.buscar(id));
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("imagemIds", produtoService.listarImagemIds(id));
        model.addAttribute("edicao", true);
        return "admin/produtos/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("produto") Produto produto,
                        BindingResult result,
                        @RequestParam(required = false) Long categoriaId,
                        @RequestParam(value = "arquivos", required = false) MultipartFile[] arquivos,
                        Model model) {
        if (categoriaId == null) {
            result.rejectValue("categoria", "produto.categoria.obrigatoria");
        }
        List<String> errosImagem = validarImagens(arquivos, 0);
        if (result.hasErrors() || !errosImagem.isEmpty()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("errosImagem", errosImagem);
            model.addAttribute("edicao", false);
            return "admin/produtos/form";
        }
        produtoService.salvar(produto, categoriaId, arquivos);
        return "redirect:/admin/produtos";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("produto") Produto produto,
                            BindingResult result,
                            @RequestParam(required = false) Long categoriaId,
                            @RequestParam(value = "arquivos", required = false) MultipartFile[] arquivos,
                            Model model) {
        if (categoriaId == null) {
            result.rejectValue("categoria", "produto.categoria.obrigatoria");
        }
        List<String> errosImagem = validarImagens(arquivos, produtoService.contarImagens(id));
        if (result.hasErrors() || !errosImagem.isEmpty()) {
            model.addAttribute("categorias", categoriaService.listar());
            model.addAttribute("imagemIds", produtoService.listarImagemIds(id));
            model.addAttribute("errosImagem", errosImagem);
            model.addAttribute("edicao", true);
            return "admin/produtos/form";
        }
        produtoService.atualizar(id, produto, categoriaId, arquivos);
        return "redirect:/admin/produtos/" + id + "/editar";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return "redirect:/admin/produtos";
    }

    @PostMapping("/{produtoId}/imagens/{imagemId}/excluir")
    public String excluirImagem(@PathVariable Long produtoId, @PathVariable Long imagemId) {
        produtoService.excluirImagem(imagemId);
        return "redirect:/admin/produtos/" + produtoId + "/editar";
    }

    /**
     * Valida os arquivos enviados: cada um deve ser uma imagem e o total
     * (existentes + novos) nao pode ultrapassar o limite. Retorna a lista de
     * mensagens de erro ja localizadas (vazia se estiver tudo certo).
     */
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
