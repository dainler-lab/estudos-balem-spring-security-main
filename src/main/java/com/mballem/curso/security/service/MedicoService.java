package com.mballem.curso.security.service;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repository;

    @Transactional(readOnly = true)
    public Medico buscarPorUsuarioId(Long id) {
        return repository.findByUsuarioId(id).orElse(new Medico());
    }

    @Transactional(readOnly = false)
    public void salvar(Medico medico) {
        repository.save(medico);
    }

    @Transactional(readOnly = false)
    public void editar(Medico medico) {
        Medico m = repository.findById(medico.getId()).get();
        m.setCrm(medico.getCrm());
        m.setDtInscricao(medico.getDtInscricao());
        m.setNome(medico.getNome());
        if (!medico.getEspecialidades().isEmpty()) {
            m.getEspecialidades().addAll(medico.getEspecialidades());
        }
    }

    @Transactional(readOnly = true)
    public Medico buscarPorEmail(String username) {
        return repository.findByUsuarioEmail(username).orElse(new Medico());
    }

    @Transactional(readOnly = false)
    public void excluirEspecialidadePorMedico(Long idMedico, Long idEspecializacao) {
        Medico medico = repository.findById(idMedico).get();
        medico.getEspecialidades().removeIf(e -> e.getId().equals(idEspecializacao));
    }

    @Transactional(readOnly = true)
    public List<Medico> getMedicosPorEspecialidade(String titulo) {
        return repository.findByMedicosPorEspecialidade(titulo);
    }

    @Transactional(readOnly = true)
    public boolean existeEspecialidadeAgendada(Long idMedico, Long idEspecializacao) {
        return repository.hasEspecialidadeAgendada(idMedico, idEspecializacao).isPresent();
    }
}
