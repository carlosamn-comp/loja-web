package com.exemplo.loja.repository;

import com.exemplo.loja.model.Categoria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Categoria, aderente a JPA via Spring Data.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);

    boolean existsByNome(String nome);
}
