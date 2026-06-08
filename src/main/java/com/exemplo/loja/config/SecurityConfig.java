package com.exemplo.loja.config;

import com.exemplo.loja.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracao do Spring Security.
 *
 * AUTENTICACAO (dois provedores, combinados num {@link ProviderManager}):
 *  1. ADMINISTRADOR — usuario EM MEMORIA ({@link InMemoryUserDetailsManager}),
 *     com a ROLE_ADMIN definida aqui no codigo. Nao existe no banco: ao logar,
 *     ja recebe a role ADMIN, sem nenhuma consulta ao banco de dados.
 *  2. LOJA / CLIENTE — usuarios do banco, carregados por {@link UsuarioDetailsService}
 *     (a role "LOJA"/"CLIENTE" ja vem gravada no proprio usuario).
 *
 * AUTORIZACAO (por role):
 *  - /api/**            : publico (AA-2)
 *  - catalogo/login/etc : publico
 *  - /admin/**          : hasRole("ADMIN")
 *  - /loja/**           : hasRole("LOJA")
 *  - /carrinho|/pedidos : hasRole("CLIENTE")
 */
@Configuration
public class SecurityConfig {

    /** Credenciais do administrador — fixas no codigo (nao ficam no banco). */
    public static final String ADMIN_EMAIL = "admin@loja.com";
    private static final String ADMIN_SENHA = "123";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager com dois provedores: primeiro tenta o admin em memoria
     * (role ADMIN); se nao for o admin, cai no provedor do banco (loja/cliente).
     */
    @Bean
    public AuthenticationManager authenticationManager(UsuarioDetailsService usuarioDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        // Provedor 1: administrador em memoria, com ROLE_ADMIN definida no login
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider();
        adminProvider.setUserDetailsService(adminUserDetailsService(passwordEncoder));
        adminProvider.setPasswordEncoder(passwordEncoder);

        // Provedor 2: lojas e clientes vindos do banco
        DaoAuthenticationProvider bancoProvider = new DaoAuthenticationProvider();
        bancoProvider.setUserDetailsService(usuarioDetailsService);
        bancoProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(adminProvider, bancoProvider);
    }

    /** Usuario administrador em memoria, com ROLE_ADMIN (sem banco de dados). */
    private UserDetailsService adminUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_SENHA))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {
        http
            .authenticationManager(authenticationManager)
            .authorizeHttpRequests(authz -> authz
                // REST-API publica (AA-2)
                .requestMatchers("/api/**").permitAll()
                // Paginas e recursos publicos
                .requestMatchers("/", "/produtos", "/produtos/**",
                        "/login", "/registro", "/registro-loja",
                        "/css/**", "/js/**", "/webjars/**",
                        "/h2-console/**").permitAll()
                // Area administrativa
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Area da loja / vendedor
                .requestMatchers("/loja/**").hasRole("LOJA")
                // Area do cliente (carrinho / pedidos)
                .requestMatchers("/carrinho/**", "/checkout/**", "/pedidos/**").hasRole("CLIENTE")
                // Demais rotas exigem autenticacao
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/produtos", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/produtos")
                .permitAll())
            // CSRF desabilitado (alinhado ao exemplo da disciplina); simplifica a
            // REST-API, o console H2 e o upload de imagens (formularios multipart).
            .csrf(AbstractHttpConfigurer::disable)
            // Console H2 e renderizado dentro de um frame
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
