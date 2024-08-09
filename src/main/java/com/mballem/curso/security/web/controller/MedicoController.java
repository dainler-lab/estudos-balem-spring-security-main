package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.EspecialidadeService;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoService service;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping("/dados")
    public String abrirPorMedico(Medico medico, ModelMap model,
                                 @AuthenticationPrincipal UserDetails user) {
        if (medico.hasNotId()) {
            medico = service.buscarPorEmail(user.getUsername());
            model.addAttribute("medico", medico);
        }
        return "medico/cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(Medico medico, RedirectAttributes attr,
                         @AuthenticationPrincipal UserDetails user) {

        if (medico.hasNotId() && medico.getUsuario().hasNotId()) {
            Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
            medico.setUsuario(usuario);
        }
        service.salvar(medico);
        attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
        attr.addFlashAttribute("medico", medico);
        return "redirect:/medicos/dados";
    }

    @PostMapping("/editar")
    public String editar(Medico medico, RedirectAttributes attr) {
        service.editar(medico);
        attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
        attr.addFlashAttribute("medico", medico);
        return "redirect:/medicos/dados";
    }

    @GetMapping("/id/{idMedico}/excluir/especializacao/{idEspecializacao}")
    public String excluirEspecialidadePorMedico(@PathVariable("idMedico") Long idMedico,
                                                @PathVariable("idEspecializacao") Long idEspecializacao,
                                                RedirectAttributes attr) {

        if (service.existeEspecialidadeAgendada(idMedico, idEspecializacao)) {
            attr.addFlashAttribute("falha", "Especialidade não pode ser removida, pois já existe um agendamento.");
        } else {

            service.excluirEspecialidadePorMedico(idMedico, idEspecializacao);
            attr.addFlashAttribute("sucesso", "Especialidade removida com sucesso.");
        }
        return "redirect:/medicos/dados";
    }

    // método para retornar os médicos por especialidade via ajax
    @GetMapping("/especialidade/titulo/{titulo}")
    public ResponseEntity<?> getMedicosPorEspecialidade(@PathVariable("titulo") String titulo) {
        return ResponseEntity.ok(service.getMedicosPorEspecialidade(titulo));
    }

}
