package com.exemplo.loja.web.admin;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Sexo;
import com.exemplo.loja.service.ClienteService;
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
 * CRUD administrativo de clientes (R1). Requer perfil ADMIN.
 *
 * Na edicao, a senha e opcional: o formulario carrega um valor sentinela
 * (nao-vazio) no campo senha apenas para satisfazer o Bean Validation, e a
 * senha so e efetivamente alterada se o campo "novaSenha" for preenchido.
 */
@Controller
@RequestMapping("/admin/clientes")
public class ClienteAdminController {

    private static final String SENHA_SENTINELA = "__MANTER__";

    private final ClienteService clienteService;

    public ClienteAdminController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "admin/clientes/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("edicao", false);
        return "admin/clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscar(id);
        cliente.setSenha(SENHA_SENTINELA); // placeholder; nao e a senha real
        model.addAttribute("cliente", cliente);
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("edicao", true);
        return "admin/clientes/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("cliente") Cliente cliente,
                        BindingResult result, Model model) {
        validarUnicidade(cliente, null, result);
        if (result.hasErrors()) {
            model.addAttribute("sexos", Sexo.values());
            model.addAttribute("edicao", false);
            return "admin/clientes/form";
        }
        clienteService.cadastrar(cliente);
        return "redirect:/admin/clientes";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("cliente") Cliente cliente,
                            BindingResult result,
                            @RequestParam(required = false) String novaSenha,
                            Model model) {
        validarUnicidade(cliente, id, result);
        if (result.hasErrors()) {
            model.addAttribute("sexos", Sexo.values());
            model.addAttribute("edicao", true);
            return "admin/clientes/form";
        }
        clienteService.atualizar(id, cliente, novaSenha);
        return "redirect:/admin/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return "redirect:/admin/clientes";
    }

    private void validarUnicidade(Cliente cliente, Long id, BindingResult result) {
        if (cliente.getEmail() != null) {
            boolean disponivel = (id == null)
                    ? clienteService.emailDisponivel(cliente.getEmail())
                    : clienteService.emailDisponivelParaEdicao(cliente.getEmail(), id);
            if (!disponivel) {
                result.rejectValue("email", "cliente.email.duplicado");
            }
        }
        if (cliente.getCpf() != null) {
            boolean disponivel = (id == null)
                    ? clienteService.cpfDisponivel(cliente.getCpf())
                    : clienteService.cpfDisponivelParaEdicao(cliente.getCpf(), id);
            if (!disponivel) {
                result.rejectValue("cpf", "cliente.cpf.duplicado");
            }
        }
    }
}
