package com.exemplo.loja.web.admin;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CRUD administrativo de categorias (R2). Requer perfil ADMIN.
 */
@Controller
@RequestMapping("/admin/categorias")
public class CategoriaAdminController {

    private final CategoriaService categoriaService;

    public CategoriaAdminController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "admin/categorias/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("edicao", false);
        return "admin/categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscar(id));
        model.addAttribute("edicao", true);
        return "admin/categorias/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("categoria") Categoria categoria,
                        BindingResult result, Model model) {
        if (categoria.getNome() != null && !categoriaService.nomeDisponivel(categoria.getNome())) {
            result.rejectValue("nome", "categoria.nome.duplicado");
        }
        if (result.hasErrors()) {
            model.addAttribute("edicao", false);
            return "admin/categorias/form";
        }
        categoriaService.salvar(categoria);
        return "redirect:/admin/categorias";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("categoria") Categoria categoria,
                            BindingResult result, Model model) {
        if (categoria.getNome() != null
                && !categoriaService.nomeDisponivelParaEdicao(categoria.getNome(), id)) {
            result.rejectValue("nome", "categoria.nome.duplicado");
        }
        if (result.hasErrors()) {
            model.addAttribute("edicao", true);
            return "admin/categorias/form";
        }
        categoriaService.atualizar(id, categoria);
        return "redirect:/admin/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return "redirect:/admin/categorias";
    }
}
