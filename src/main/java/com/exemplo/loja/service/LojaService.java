package com.exemplo.loja.service;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.repository.AdministradorRepository;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.LojaRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do cadastro de lojas/vendedores, reaproveitadas pelo
 * auto-cadastro (/registro-loja) e pelo CRUD administrativo (/admin/lojas).
 */
@Service
public class LojaService {

    private final LojaRepository lojaRepo;
    private final AdministradorRepository administradorRepo;
    private final ClienteRepository clienteRepo;
    private final PasswordEncoder passwordEncoder;

    public LojaService(LojaRepository lojaRepo, AdministradorRepository administradorRepo,
                       ClienteRepository clienteRepo, PasswordEncoder passwordEncoder) {
        this.lojaRepo = lojaRepo;
        this.administradorRepo = administradorRepo;
        this.clienteRepo = clienteRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Loja> listar() {
        return lojaRepo.findAll();
    }

    public Loja buscar(Long id) {
        return lojaRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Loja nao encontrada: " + id));
    }

    public Loja buscarPorEmail(String email) {
        return lojaRepo.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Loja nao encontrada: " + email));
    }

    /** E-mail livre entre os tres perfis (admin, loja, cliente). */
    public boolean emailDisponivel(String email) {
        return !administradorRepo.existsByEmail(email)
                && !lojaRepo.existsByEmail(email)
                && !clienteRepo.existsByEmail(email);
    }

    public boolean emailDisponivelParaEdicao(String email, Long lojaId) {
        if (administradorRepo.existsByEmail(email) || clienteRepo.existsByEmail(email)) {
            return false;
        }
        return lojaRepo.findByEmail(email)
                .map(l -> l.getId().equals(lojaId))
                .orElse(true);
    }

    public boolean cnpjDisponivel(String cnpj) {
        return !lojaRepo.existsByCnpj(cnpj);
    }

    public boolean cnpjDisponivelParaEdicao(String cnpj, Long lojaId) {
        return lojaRepo.findAll().stream()
                .filter(l -> cnpj.equals(l.getCnpj()))
                .allMatch(l -> l.getId().equals(lojaId));
    }

    @Transactional
    public Loja cadastrar(Loja loja) {
        loja.setId(null);
        loja.setSenha(passwordEncoder.encode(loja.getSenha()));
        return lojaRepo.save(loja);
    }

    @Transactional
    public Loja atualizar(Long id, Loja dados, String novaSenha) {
        Loja existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setCnpj(dados.getCnpj());
        existente.setDescricao(dados.getDescricao());
        if (novaSenha != null && !novaSenha.isBlank()) {
            existente.setSenha(passwordEncoder.encode(novaSenha));
        }
        return lojaRepo.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        lojaRepo.deleteById(id);
    }
}
