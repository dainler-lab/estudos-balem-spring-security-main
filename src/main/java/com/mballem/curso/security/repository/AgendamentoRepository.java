package com.mballem.curso.security.repository;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT h FROM Horario h " +
            "WHERE NOT EXISTS (" +
                "SELECT a.horario.id " +
                "FROM Agendamento a " +
                "WHERE a.medico.id = :id AND " +
                "a.dataConsulta = :data AND " +
                "a.horario.id = h.id) " +
                "ORDER BY h.horaMinuto ASC"
    )
    List<Horario> findByMedicoIdAndDataNotHorarioAgendado(Long id, LocalDate data);

    @Query("SELECT a.id AS id, " +
            "a.paciente AS paciente, " +
            "CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) AS dataConsulta, " +
            "a.medico AS medico, " +
            "a.especialidade AS especialidade " +
            "FROM Agendamento a " +
            "WHERE a.paciente.usuario.email LIKE :email"
    )
    Page<HistoricoPaciente> findHistoricoByPacienteEmail(String email, Pageable pageable);

    @Query("SELECT a.id AS id, " +
            "a.paciente AS paciente, " +
            "CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) AS dataConsulta, " +
            "a.medico AS medico, " +
            "a.especialidade AS especialidade " +
            "FROM Agendamento a " +
            "WHERE a.medico.usuario.email LIKE :email"
    )
    Page<HistoricoPaciente> findHistoricoByMedicoEmail(String email, Pageable pageable);

    @Query("SELECT a FROM Agendamento a " +
            "WHERE " +
            "(a.id = :id AND a.paciente.usuario.email LIKE :email) " +
            "OR " +
            "(a.id = :id AND a.medico.usuario.email LIKE :email)"
    )
    Optional <Agendamento> findByIdAndPacienteOrMedicoEmail(Long id, String email);
}
