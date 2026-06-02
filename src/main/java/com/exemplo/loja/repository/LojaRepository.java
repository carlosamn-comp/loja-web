package com.exemplo.loja.repository;

import com.exemplo.loja.model.Loja;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Loja, aderente a JPA via Spring Data.
 */
@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {

    Optional<Loja> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);
}
