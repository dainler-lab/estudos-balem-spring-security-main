package com.mballem.curso.security.service;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.exception.AcessoNegadoException;
import com.mballem.curso.security.repository.AgendamentoRepository;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private Datatables datatables;

    @Transactional(readOnly = true)
    public List<Horario> buscarHorariosNaoAgendadosPorMedicoEData(Long id, LocalDate data) {
        return agendamentoRepository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
    }

    @Transactional(readOnly = false)
    public void salvar(Agendamento agendamento) {
        agendamentoRepository.save(agendamento);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarHistoricoPorPacienteEmail(String email, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
        Page<HistoricoPaciente> page = agendamentoRepository.findHistoricoByPacienteEmail(email, datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarHistoricoPorMedicoEmail(String email, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
        Page<HistoricoPaciente> page = agendamentoRepository.findHistoricoByMedicoEmail(email, datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id).get();
    }

    @Transactional(readOnly = false)
    public void editar(Agendamento agendamentoEditado, String email) {
        Agendamento agendamentoCadastrado = buscarPorIdEUsuario(agendamentoEditado.getId(), email);
        agendamentoCadastrado.setDataConsulta(agendamentoEditado.getDataConsulta());
        agendamentoCadastrado.setEspecialidade(agendamentoEditado.getEspecialidade());
        agendamentoCadastrado.setHorario(agendamentoEditado.getHorario());
        agendamentoCadastrado.setMedico(agendamentoEditado.getMedico());
    }

    @Transactional(readOnly = true)
    public Agendamento buscarPorIdEUsuario(Long id, String email) {
        return agendamentoRepository.findByIdAndPacienteOrMedicoEmail(id, email)
                .orElseThrow(() -> new AcessoNegadoException("Acesso negado ao usuário: " + email));
    }

    @Transactional(readOnly = false)
    public void remover(Long id) {
        agendamentoRepository.deleteById(id);
    }
}
