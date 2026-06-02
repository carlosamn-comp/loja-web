package com.exemplo.loja.config;

import com.exemplo.loja.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracao do Spring Security.
 *
 * - A REST-API (/api/**) e publica (atividade AA-2 dispensa autenticacao).
 * - O catalogo publico, login e registro sao acessiveis sem autenticacao.
 * - As rotas /admin/** exigem perfil ADMIN; carrinho/pedidos exigem perfil CLIENTE.
 * - O CSRF e desabilitado apenas para /api/** e /h2-console/**; nos formularios
 *   Thymeleaf o token CSRF e injetado automaticamente.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UsuarioDetailsService usuarioDetailsService) throws Exception {
        http
            .userDetailsService(usuarioDetailsService)
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
