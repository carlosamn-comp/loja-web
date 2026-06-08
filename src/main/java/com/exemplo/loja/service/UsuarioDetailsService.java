package com.exemplo.loja.service;

import com.exemplo.loja.model.Usuario;
import com.exemplo.loja.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Autenticacao do sistema.
 *
 * O ADMINISTRADOR e definido DIRETAMENTE NESTE ARQUIVO (email/senha/role abaixo),
 * portanto seu login NAO consulta o banco de dados.
 *
 * Os demais usuarios (lojas e clientes) sao resolvidos com UMA UNICA consulta
 * ({@code usuarioRepo.findByEmail}); a role ja vem gravada no proprio usuario
 * (definida pelo construtor da subclasse), entao nao e preciso descobrir o perfil
 * tentando varios repositorios.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    /** Credenciais do administrador — fixas no codigo (nao ficam no banco). */
    public static final String ADMIN_EMAIL = "admin@loja.com";
    private static final String ADMIN_SENHA = "123";

    private final UsuarioRepository usuarioRepo;
    private final String adminSenhaHash;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        // codifica a senha do admin uma unica vez, na construcao do bean
        this.adminSenhaHash = passwordEncoder.encode(ADMIN_SENHA);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) Administrador: resolvido direto no arquivo, sem tocar o banco
        if (ADMIN_EMAIL.equalsIgnoreCase(email)) {
            return User.withUsername(ADMIN_EMAIL)
                    .password(adminSenhaHash)
                    .roles("ADMIN")
                    .build();
        }

        // 2) Loja ou Cliente: uma unica consulta; a role ja vem no usuario
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRole()) // "LOJA" -> ROLE_LOJA, "CLIENTE" -> ROLE_CLIENTE
                .build();
    }
}
