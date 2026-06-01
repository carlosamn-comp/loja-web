package com.exemplo.loja.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Disponibiliza, para todos os controllers MVC (pacote web e subpacotes), a URI
 * atual da requisicao. Usado pelo seletor de idioma para manter o usuario na
 * mesma pagina ao trocar PT/EN (o objeto #request foi removido no Thymeleaf 3.1).
 */
@ControllerAdvice(basePackages = "com.exemplo.loja.web")
public class ControllerModelAdvice {

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
