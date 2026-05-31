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
}
