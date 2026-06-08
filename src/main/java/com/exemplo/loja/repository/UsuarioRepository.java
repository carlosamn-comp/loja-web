package com.exemplo.loja.repository;

import com.exemplo.loja.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade-mae {@link Usuario}. Como a heranca usa SINGLE_TABLE, este
 * repositorio enxerga TODOS os usuarios (lojas e clientes) numa unica tabela.
 *
 * Usado para:
 *  - autenticacao: uma unica consulta por e-mail devolve o usuario com a sua role;
 *  - unicidade de e-mail entre lojas e clientes (existsByEmail abrange ambos).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
