package com.exemplo.loja.service;

import com.exemplo.loja.model.Usuario;
import com.exemplo.loja.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega os usuarios do BANCO (lojas e clientes) para o Spring Security.
 *
 * Uma unica consulta ({@code findByEmail}) resolve o usuario, e a role
 * ("LOJA"/"CLIENTE") ja vem gravada nele (definida no construtor da subclasse).
 *
 * O ADMINISTRADOR nao passa por aqui: ele e um usuario em memoria com ROLE_ADMIN,
 * configurado em {@link com.exemplo.loja.config.SecurityConfig} (sem banco).
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRole()) // "LOJA" -> ROLE_LOJA, "CLIENTE" -> ROLE_CLIENTE
                .build();
    }
}
