package com.exemplo.loja.service;

import com.exemplo.loja.model.Administrador;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Loja;
import com.exemplo.loja.repository.AdministradorRepository;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.LojaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servico de autenticacao que resolve, a partir de um e-mail, um dos tres
 * perfis: {@link Administrador} (ROLE_ADMIN), {@link Loja} (ROLE_LOJA) ou
 * {@link Cliente} (ROLE_CLIENTE).
 *
 * Precedencia: administrador, depois loja, depois cliente. Os cadastros
 * impedem reutilizar um e-mail ja usado por outro perfil.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepo;
    private final LojaRepository lojaRepo;
    private final ClienteRepository clienteRepo;

    public UsuarioDetailsService(AdministradorRepository administradorRepo,
                                 LojaRepository lojaRepo,
                                 ClienteRepository clienteRepo) {
        this.administradorRepo = administradorRepo;
        this.lojaRepo = lojaRepo;
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

        Loja loja = lojaRepo.findByEmail(email).orElse(null);
        if (loja != null) {
            return User.withUsername(loja.getEmail())
                    .password(loja.getSenha())
                    .roles("LOJA")
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
