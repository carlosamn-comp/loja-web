package com.exemplo.loja.service;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.repository.AdministradorRepository;
import com.exemplo.loja.repository.ClienteRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do cadastro de clientes, reaproveitadas pelo auto-cadastro
 * (/registro) e pelo CRUD administrativo (/admin/clientes).
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final AdministradorRepository administradorRepo;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepo,
                          AdministradorRepository administradorRepo,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepo = clienteRepo;
        this.administradorRepo = administradorRepo;
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

    /** True se o e-mail nao esta em uso por nenhum cliente nem administrador. */
    public boolean emailDisponivel(String email) {
        return !administradorRepo.existsByEmail(email) && !clienteRepo.existsByEmail(email);
    }

    /** Verifica disponibilidade do e-mail considerando que o proprio cliente pode mante-lo. */
    public boolean emailDisponivelParaEdicao(String email, Long clienteId) {
        if (administradorRepo.existsByEmail(email)) {
            return false;
        }
        return clienteRepo.findByEmail(email)
                .map(c -> c.getId().equals(clienteId))
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

    /**
     * Atualiza um cliente existente. A senha so e re-codificada se uma nova
     * senha (nao vazia) for informada; caso contrario a senha atual e mantida.
     */
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
