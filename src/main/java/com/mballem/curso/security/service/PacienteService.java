package com.mballem.curso.security.service;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Transactional(readOnly = false)
    public Paciente buscarPorUsuarioEmail(String email) {
        return repository.findByUsuarioEmail(email).orElse(new Paciente());
    }

    @Transactional(readOnly = false)
    public void salvar(Paciente paciente) {
        repository.save(paciente);
    }

    @Transactional(readOnly = false)
    public void editar(Paciente paciente) {
        Paciente p = repository.findById(paciente.getId()).get();
        p.setNome(paciente.getNome());
        p.setDtNascimento(paciente.getDtNascimento());
    }
}
