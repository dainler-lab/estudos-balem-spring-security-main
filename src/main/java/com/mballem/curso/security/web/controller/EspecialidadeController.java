package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.service.EspecialidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping({"", "/"})
    public String abrir(Especialidade especialidade) {
        return "especialidade/especialidade";
    }

    @PostMapping("/salvar")
    public String salvar(Especialidade especialidade, RedirectAttributes attr) {
        especialidadeService.salvar(especialidade);
        attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
        return "redirect:/especialidades";
    }

    @GetMapping("/datatables/server")
    public ResponseEntity<?> getEspecialidades(HttpServletRequest request) {
        return ResponseEntity.ok(especialidadeService.buscarEspecialidades(request));
    }

    @GetMapping("/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("especialidade", especialidadeService.buscarPorId(id));
        return "especialidade/especialidade";
    }

    @GetMapping("/excluir/{id}")
    public String preEditar(@PathVariable("id") Long id, RedirectAttributes attr) {
        especialidadeService.remover(id);
        attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
        return "redirect:/especialidades";
    }

    @GetMapping("/titulo")
    public ResponseEntity<?> getEspecialidadesPorTermo(@RequestParam("termo") String termo) {
        List<String> especialidades = especialidadeService.buscarEspecialidadesByTermo(termo);
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/datatables/server/medico/{id}")
    public ResponseEntity<?> getEspecialidadesPorMedico(@PathVariable("id") Long id, HttpServletRequest request) {
        return ResponseEntity.ok(especialidadeService.buscarEspecialidadesPorMedico(id, request));
    }
}
