package com.exemplo.loja.web;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Loja;
import com.exemplo.loja.model.Sexo;
import com.exemplo.loja.service.ClienteService;
import com.exemplo.loja.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controla as telas de autenticacao: login, auto-cadastro de cliente e
 * auto-cadastro de loja/vendedor.
 */
@Controller
public class AuthController {

    private final ClienteService clienteService;
    private final LojaService lojaService;

    public AuthController(ClienteService clienteService, LojaService lojaService) {
        this.clienteService = clienteService;
        this.lojaService = lojaService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ---------------- Cadastro de cliente ----------------

    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("sexos", Sexo.values());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("cliente") Cliente cliente,
                            BindingResult result, Model model) {
        if (cliente.getEmail() != null && !clienteService.emailDisponivel(cliente.getEmail())) {
            result.rejectValue("email", "cliente.email.duplicado");
        }
        if (cliente.getCpf() != null && !clienteService.cpfDisponivel(cliente.getCpf())) {
            result.rejectValue("cpf", "cliente.cpf.duplicado");
        }
        if (result.hasErrors()) {
            model.addAttribute("sexos", Sexo.values());
            return "registro";
        }
        clienteService.cadastrar(cliente);
        return "redirect:/login?registrado";
    }

    // ---------------- Cadastro de loja ----------------

    @GetMapping("/registro-loja")
    public String formRegistroLoja(Model model) {
        model.addAttribute("loja", new Loja());
        return "registro-loja";
    }

    @PostMapping("/registro-loja")
    public String registrarLoja(@Valid @ModelAttribute("loja") Loja loja,
                                BindingResult result) {
        if (loja.getEmail() != null && !lojaService.emailDisponivel(loja.getEmail())) {
            result.rejectValue("email", "loja.email.duplicado");
        }
        if (loja.getCnpj() != null && !lojaService.cnpjDisponivel(loja.getCnpj())) {
            result.rejectValue("cnpj", "loja.cnpj.duplicado");
        }
        if (result.hasErrors()) {
            return "registro-loja";
        }
        lojaService.cadastrar(loja);
        return "redirect:/login?registrado";
    }
}
