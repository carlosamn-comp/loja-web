package com.exemplo.loja.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tratamento global de erros das telas Web (Thymeleaf): registra o erro no
 * console e renderiza uma pagina de erro amigavel (templates/error.html).
 *
 * Erros de validacao de formulario NAO passam por aqui: sao tratados nos
 * proprios controllers via BindingResult (retornando ao formulario).
 */
@ControllerAdvice(basePackages = "com.exemplo.loja.web")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String msg(String code) {
        return messageSource.getMessage(code, null, code, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recursoNaoEncontrado(IllegalArgumentException ex, Model model) {
        log.warn("Recurso nao encontrado: {}", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("mensagem", msg("erro.recursoNaoEncontrado"));
        return "error";
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String estoqueInsuficiente(EstoqueInsuficienteException ex, Model model) {
        log.warn("Estoque insuficiente: {}", ex.getMessage());
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("mensagem", msg("erro.estoqueInsuficiente").replace("{0}", ex.getProdutoNome()));
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String integridade(DataIntegrityViolationException ex, Model model) {
        log.error("Violacao de integridade de dados", ex);
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("mensagem", msg("erro.integridade"));
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String erroGenerico(Exception ex, Model model) {
        log.error("Erro inesperado", ex);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("mensagem", msg("erro.mensagem"));
        return "error";
    }
}
