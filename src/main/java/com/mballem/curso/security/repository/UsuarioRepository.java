package com.mballem.curso.security.repository;

import com.mballem.curso.security.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.perfis p WHERE u.email LIKE :search% OR p.desc LIKE :search%")
    Page<Usuario> findByEmailOrPerfil(String search, Pageable pageable);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.perfis p WHERE u.id= :usuarioId AND p.id IN :perfisId")
    Optional<Usuario> findByIdAndPerfis(Long usuarioId, Long[] perfisId);

    @Query("SELECT u FROM Usuario u WHERE u.email LIKE :email AND u.ativo = true")
    Optional<Usuario> findByEmailAndAtivo(String email);
}
