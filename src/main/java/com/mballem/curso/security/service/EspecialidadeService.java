package com.mballem.curso.security.service;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class EspecialidadeService {

    @Autowired
    Datatables datatables;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Transactional(readOnly = false)
    public void salvar(Especialidade especialidade) {
        especialidadeRepository.save(especialidade);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEspecialidades(HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
        Page<?> page = datatables.getSearch().isEmpty()
                ? especialidadeRepository.findAll(datatables.getPageable())
                : especialidadeRepository.findByTitulo(datatables.getSearch(), datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Especialidade buscarPorId(Long id) {
        return especialidadeRepository.findById(id).get();
    }

    @Transactional(readOnly = false)
    public void remover(Long id) {
        especialidadeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> buscarEspecialidadesByTermo(String termo) {
        return especialidadeRepository.findEspecialidadesByTermo(termo);
    }

    @Transactional(readOnly = true)
    public Collection<? extends Especialidade> buscarPorTitulos(String[] titulos) {
        return especialidadeRepository.findByTitulos(titulos);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEspecialidadesPorMedico(Long id, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
        Page<Especialidade> page = especialidadeRepository.findByIdMedico(id, datatables.getPageable());
        return datatables.getResponse(page);
    }
}
