package com.exemplo.loja.web.restclient;

import com.exemplo.loja.client.CategoriaApiClient;
import com.exemplo.loja.client.CategoriaDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientResponseException;

/**
 * Aplicacao web CLIENTE da REST-API (atividade T8): telas Thymeleaf que fazem o
 * CRUD de categorias consumindo a REST-API (T7) via {@link CategoriaApiClient}
 * (RestClient) — sem tocar no banco/repositorio diretamente.
 */
@Controller
@RequestMapping("/rest-client/categorias")
public class CategoriaClientController {

    private final CategoriaApiClient api;

    public CategoriaClientController(CategoriaApiClient api) {
        this.api = api;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", api.listar());
        return "restclient/categorias/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("categoria", new CategoriaDto());
        model.addAttribute("edicao", false);
        return "restclient/categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", api.buscar(id));
        model.addAttribute("edicao", true);
        return "restclient/categorias/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("categoria") CategoriaDto categoria,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("edicao", false);
            return "restclient/categorias/form";
        }
        try {
            api.criar(categoria);
        } catch (RestClientResponseException ex) {
            model.addAttribute("edicao", false);
            model.addAttribute("erroApi", mensagemApi(ex));
            return "restclient/categorias/form";
        }
        return "redirect:/rest-client/categorias";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("categoria") CategoriaDto categoria,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("edicao", true);
            return "restclient/categorias/form";
        }
        try {
            api.atualizar(id, categoria);
        } catch (RestClientResponseException ex) {
            model.addAttribute("edicao", true);
            model.addAttribute("erroApi", mensagemApi(ex));
            return "restclient/categorias/form";
        }
        return "redirect:/rest-client/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        api.excluir(id);
        return "redirect:/rest-client/categorias";
    }

    /** Status HTTP devolvido pela API (ex.: 409 = nome duplicado). */
    private String mensagemApi(RestClientResponseException ex) {
        return "A API retornou " + ex.getStatusCode().value()
                + " (" + ex.getStatusText() + ").";
    }
}
