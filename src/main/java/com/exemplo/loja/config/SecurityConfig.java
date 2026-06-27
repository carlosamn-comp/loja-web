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
 * AUTENTICACAO: todos os usuarios (ADMIN, LOJA, CLIENTE) ficam no banco, na
 * tabela `usuario` (heranca single-table). O {@link UsuarioDetailsService}
 * carrega o usuario por e-mail e usa a `role` ja gravada nele. As senhas sao
 * armazenadas criptografadas com BCrypt.
 *
 * AUTORIZACAO (por role):
 *  - /api/**, catalogo, login, registros, /rest-client/** : publico
 *  - /admin/**            : hasRole("ADMIN")
 *  - /loja/**             : hasRole("LOJA")
 *  - /carrinho|/pedidos   : hasRole("CLIENTE")
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
                // REST-API publica (AA-2) e app cliente da REST-API (T8)
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/", "/produtos", "/produtos/**",
                        "/login", "/registro", "/registro-loja",
                        "/rest-client/**",
                        "/css/**", "/js/**", "/webjars/**",
                        "/h2-console/**").permitAll()
                // Areas por perfil
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/loja/**").hasRole("LOJA")
                .requestMatchers("/carrinho/**", "/checkout/**", "/pedidos/**").hasRole("CLIENTE")
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
