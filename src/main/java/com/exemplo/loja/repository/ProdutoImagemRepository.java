package com.exemplo.loja.repository;

import com.exemplo.loja.model.ProdutoImagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO das imagens de produto.
 */
@Repository
public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {

    long countByProdutoId(Long produtoId);

    /** Retorna apenas os ids das imagens (projecao leve, sem carregar os bytes). */
    @Query("SELECT i.id FROM ProdutoImagem i WHERE i.produto.id = :produtoId ORDER BY i.id")
    List<Long> findIdsByProdutoId(@Param("produtoId") Long produtoId);
}
