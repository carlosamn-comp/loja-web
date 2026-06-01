package com.exemplo.loja.repository;

import com.exemplo.loja.model.Produto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Produto, aderente a JPA via Spring Data.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Query method derivado do nome
    List<Produto> findByNomeContainingIgnoreCase(String trecho);

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByPrecoLessThanEqual(BigDecimal precoMaximo);

    // Consulta JPQL explicita
    @Query("SELECT p FROM Produto p WHERE p.estoque > 0 ORDER BY p.nome")
    List<Produto> listarDisponiveis();

    @Query("SELECT p FROM Produto p WHERE p.categoria.nome = :nomeCategoria")
    List<Produto> buscarPorNomeCategoria(@Param("nomeCategoria") String nomeCategoria);

    /**
     * Filtro combinado (R4): todos os parametros sao opcionais (nulos sao ignorados).
     */
    @Query("SELECT p FROM Produto p WHERE "
            + "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND "
            + "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND "
            + "(:precoMin IS NULL OR p.preco >= :precoMin) AND "
            + "(:precoMax IS NULL OR p.preco <= :precoMax) "
            + "ORDER BY p.nome")
    List<Produto> filtrar(@Param("nome") String nome,
                          @Param("categoriaId") Long categoriaId,
                          @Param("precoMin") BigDecimal precoMin,
                          @Param("precoMax") BigDecimal precoMax);
}
