package com.exemplo.loja.rest;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.repository.LojaRepository;
import com.exemplo.loja.service.LojaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST-API de lojas/vendedores (CRUD completo). A senha e recebida no corpo
 * (WRITE_ONLY) e armazenada com hash BCrypt; nunca e devolvida nas respostas.
 */
@RestController
@RequestMapping("/api/lojas")
public class LojaRestController {

    private final LojaRepository lojaRepo;
    private final LojaService lojaService;

    public LojaRestController(LojaRepository lojaRepo, LojaService lojaService) {
        this.lojaRepo = lojaRepo;
        this.lojaService = lojaService;
    }

    @GetMapping
    public List<Loja> listar() {
        return lojaRepo.findAll();
    }

    @GetMapping("/{id}")
    public Loja buscar(@PathVariable Long id) {
        return lojaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loja nao encontrada: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loja criar(@Valid @RequestBody Loja loja) {
        return lojaService.cadastrar(loja);
    }

    @PutMapping("/{id}")
    public Loja atualizar(@PathVariable Long id, @Valid @RequestBody Loja dados) {
        if (!lojaRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja nao encontrada: " + id);
        }
        return lojaService.atualizar(id, dados, dados.getSenha());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        if (!lojaRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja nao encontrada: " + id);
        }
        lojaRepo.deleteById(id);
    }
}
