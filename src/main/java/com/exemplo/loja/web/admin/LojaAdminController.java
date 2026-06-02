package com.exemplo.loja.web.admin;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * CRUD administrativo de lojas/vendedores (R2). Requer perfil ADMIN.
 *
 * Na edicao a senha e opcional (placeholder sentinela apenas para satisfazer o
 * Bean Validation); so e alterada se "novaSenha" vier preenchido.
 */
@Controller
@RequestMapping("/admin/lojas")
public class LojaAdminController {

    private static final String SENHA_SENTINELA = "__MANTER__";

    private final LojaService lojaService;

    public LojaAdminController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lojas", lojaService.listar());
        return "admin/lojas/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("loja", new Loja());
        model.addAttribute("edicao", false);
        return "admin/lojas/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        Loja loja = lojaService.buscar(id);
        loja.setSenha(SENHA_SENTINELA);
        model.addAttribute("loja", loja);
        model.addAttribute("edicao", true);
        return "admin/lojas/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("loja") Loja loja,
                        BindingResult result, Model model) {
        validarUnicidade(loja, null, result);
        if (result.hasErrors()) {
            model.addAttribute("edicao", false);
            return "admin/lojas/form";
        }
        lojaService.cadastrar(loja);
        return "redirect:/admin/lojas";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("loja") Loja loja,
                            BindingResult result,
                            @RequestParam(required = false) String novaSenha,
                            Model model) {
        validarUnicidade(loja, id, result);
        if (result.hasErrors()) {
            model.addAttribute("edicao", true);
            return "admin/lojas/form";
        }
        lojaService.atualizar(id, loja, novaSenha);
        return "redirect:/admin/lojas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        lojaService.excluir(id);
        return "redirect:/admin/lojas";
    }

    private void validarUnicidade(Loja loja, Long id, BindingResult result) {
        if (loja.getEmail() != null) {
            boolean ok = (id == null)
                    ? lojaService.emailDisponivel(loja.getEmail())
                    : lojaService.emailDisponivelParaEdicao(loja.getEmail(), id);
            if (!ok) {
                result.rejectValue("email", "loja.email.duplicado");
            }
        }
        if (loja.getCnpj() != null) {
            boolean ok = (id == null)
                    ? lojaService.cnpjDisponivel(loja.getCnpj())
                    : lojaService.cnpjDisponivelParaEdicao(loja.getCnpj(), id);
            if (!ok) {
                result.rejectValue("cnpj", "loja.cnpj.duplicado");
            }
        }
    }
}
