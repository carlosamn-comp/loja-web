package com.exemplo.loja.service;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.repository.LojaRepository;
import com.exemplo.loja.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do cadastro de lojas/vendedores, reaproveitadas pelo
 * auto-cadastro (/registro-loja) e pelo CRUD administrativo (/admin/lojas).
 *
 * O e-mail e unico entre TODOS os usuarios (verificado via {@link UsuarioRepository})
 * e nao pode ser o do administrador; o CNPJ e unico entre as lojas.
 */
@Service
public class LojaService {

    private final LojaRepository lojaRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public LojaService(LojaRepository lojaRepo, UsuarioRepository usuarioRepo,
                       PasswordEncoder passwordEncoder) {
        this.lojaRepo = lojaRepo;
        this.usuarioRepo = usuarioRepo;
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

    public boolean emailDisponivel(String email) {
        return !usuarioRepo.existsByEmail(email);
    }

    public boolean emailDisponivelParaEdicao(String email, Long lojaId) {
        return usuarioRepo.findByEmail(email)
                .map(u -> u.getId().equals(lojaId))
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
