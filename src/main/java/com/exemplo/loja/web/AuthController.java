package com.exemplo.loja.web;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Sexo;
import com.exemplo.loja.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controla as telas de autenticacao: login e auto-cadastro de cliente.
 */
@Controller
public class AuthController {

    private final ClienteService clienteService;

    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("sexos", Sexo.values());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("cliente") Cliente cliente,
                            BindingResult result, Model model) {
        // Validacoes de unicidade (alem do Bean Validation)
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
}
