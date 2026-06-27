package com.exemplo.loja.service;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do cadastro de clientes, reaproveitadas pelo auto-cadastro
 * (/registro) e pelo CRUD administrativo (/admin/clientes).
 *
 * A unicidade de e-mail e verificada via {@link UsuarioRepository}, que abrange
 * TODOS os usuarios (admin, lojas e clientes) por compartilharem a tabela "usuario".
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepo, UsuarioRepository usuarioRepo,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepo = clienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> listar() {
        return clienteRepo.findAll();
    }

    public Cliente buscar(Long id) {
        return clienteRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Cliente nao encontrado: " + id));
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepo.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Cliente nao encontrado: " + email));
    }

    /** E-mail livre se nao existir em nenhum usuario (admin, loja ou cliente). */
    public boolean emailDisponivel(String email) {
        return !usuarioRepo.existsByEmail(email);
    }

    /** Disponibilidade considerando que o proprio cliente pode manter o e-mail. */
    public boolean emailDisponivelParaEdicao(String email, Long clienteId) {
        return usuarioRepo.findByEmail(email)
                .map(u -> u.getId().equals(clienteId))
                .orElse(true);
    }

    public boolean cpfDisponivel(String cpf) {
        return !clienteRepo.existsByCpf(cpf);
    }

    public boolean cpfDisponivelParaEdicao(String cpf, Long clienteId) {
        return clienteRepo.findAll().stream()
                .filter(c -> cpf.equals(c.getCpf()))
                .allMatch(c -> c.getId().equals(clienteId));
    }

    /** Cadastra um novo cliente, aplicando hash BCrypt na senha. */
    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        cliente.setId(null);
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        return clienteRepo.save(cliente);
    }

    /** A senha so e re-codificada se uma nova senha (nao vazia) for informada. */
    @Transactional
    public Cliente atualizar(Long id, Cliente dados, String novaSenha) {
        Cliente existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setCpf(dados.getCpf());
        existente.setTelefone(dados.getTelefone());
        existente.setSexo(dados.getSexo());
        existente.setDataNascimento(dados.getDataNascimento());
        if (novaSenha != null && !novaSenha.isBlank()) {
            existente.setSenha(passwordEncoder.encode(novaSenha));
        }
        return clienteRepo.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        clienteRepo.deleteById(id);
    }
}
