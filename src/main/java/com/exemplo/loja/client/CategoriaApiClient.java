package com.exemplo.loja.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Cliente da REST-API de categorias (atividade T8).
 *
 * Usa o {@link RestClient} do Spring (Spring Framework 6.1+) para consumir os
 * endpoints HTTP `/api/categorias` da própria aplicação (a REST-API da T7),
 * em vez de acessar o banco diretamente. A URL base vem de `app.api.base-url`.
 */
@Service
public class CategoriaApiClient {

    private final RestClient restClient;

    public CategoriaApiClient(RestClient.Builder builder,
                              @Value("${app.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /** GET /api/categorias */
    public List<CategoriaDto> listar() {
        return restClient.get()
                .uri("/api/categorias")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CategoriaDto>>() {});
    }

    /** GET /api/categorias/{id} */
    public CategoriaDto buscar(Long id) {
        return restClient.get()
                .uri("/api/categorias/{id}", id)
                .retrieve()
                .body(CategoriaDto.class);
    }

    /** POST /api/categorias */
    public void criar(CategoriaDto categoria) {
        restClient.post()
                .uri("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoria)
                .retrieve()
                .toBodilessEntity();
    }

    /** PUT /api/categorias/{id} */
    public void atualizar(Long id, CategoriaDto categoria) {
        restClient.put()
                .uri("/api/categorias/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoria)
                .retrieve()
                .toBodilessEntity();
    }

    /** DELETE /api/categorias/{id} */
    public void excluir(Long id) {
        restClient.delete()
                .uri("/api/categorias/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
