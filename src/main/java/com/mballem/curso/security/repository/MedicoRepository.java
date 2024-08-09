package com.mballem.curso.security.repository;

import com.mballem.curso.security.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @Query("SELECT m FROM Medico m WHERE m.usuario.id = :id")
    Optional<Medico> findByUsuarioId(Long id);

    @Query("SELECT m FROM Medico m WHERE m.usuario.email = :email")
    Optional<Medico> findByUsuarioEmail(String email);

    @Query("SELECT DISTINCT m FROM Medico m " +
            "JOIN m.especialidades e " +
            "WHERE e.titulo LIKE :titulo " +
            "AND m.usuario.ativo = true")
    List<Medico> findByMedicosPorEspecialidade(String titulo);

    @Query("SELECT m.id FROM Medico m " +
            "JOIN m.especialidades e " +
            "JOIN m.agendamentos a " +
            "WHERE " +
            "a.medico.id = :idMedico AND a.especialidade.id = :idEspecializacao "
    )
    Optional <Long> hasEspecialidadeAgendada(Long idMedico, Long idEspecializacao);
}
