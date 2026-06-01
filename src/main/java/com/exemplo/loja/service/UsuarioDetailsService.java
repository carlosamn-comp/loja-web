package com.exemplo.loja.service;

import com.exemplo.loja.model.Administrador;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.repository.AdministradorRepository;
import com.exemplo.loja.repository.ClienteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servico de autenticacao que resolve, a partir de um e-mail, tanto um
 * {@link Administrador} (perfil ROLE_ADMIN) quanto um {@link Cliente}
 * (perfil ROLE_CLIENTE).
 *
 * O administrador tem precedencia: caso o mesmo e-mail exista nas duas tabelas,
 * o login e tratado como administrador. O cadastro de clientes impede a criacao
 * de um cliente com e-mail ja usado por um administrador.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepo;
    private final ClienteRepository clienteRepo;

    public UsuarioDetailsService(AdministradorRepository administradorRepo,
                                 ClienteRepository clienteRepo) {
        this.administradorRepo = administradorRepo;
        this.clienteRepo = clienteRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrador admin = administradorRepo.findByEmail(email).orElse(null);
        if (admin != null) {
            return User.withUsername(admin.getEmail())
                    .password(admin.getSenha())
                    .roles("ADMIN")
                    .build();
        }

        Cliente cliente = clienteRepo.findByEmail(email).orElse(null);
        if (cliente != null) {
            return User.withUsername(cliente.getEmail())
                    .password(cliente.getSenha())
                    .roles("CLIENTE")
                    .build();
        }

        throw new UsernameNotFoundException("Usuario nao encontrado: " + email);
    }
}
