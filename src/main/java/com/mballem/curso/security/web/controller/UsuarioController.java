package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private MedicoService medicoService;

    // abrir o cadastro de usuários (medico/admin/paciente)
    @GetMapping("/novo/cadastro/usuario")
    public String cadastroPorAdminParaMedicoPaciente(Usuario usuario) {
        return "usuario/cadastro";
    }

    @GetMapping("/lista")
    public String listarUsuarios() {
        return "usuario/lista";
    }

    @GetMapping("/datatables/server/usuarios")
    public ResponseEntity<?> listarUsuariosDatatable(HttpServletRequest request) {
        return ResponseEntity.ok(service.buscarTodos(request));
    }

    // salvar o cadastro de usuários por admin
    @PostMapping("/cadastro/salvar")
    public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
        List<Perfil> perfis = usuario.getPerfis();
        if (perfis.size() > 2
                || perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L)))
                || perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))
        ) {
            attr.addFlashAttribute("falha", "Paciente não pode ter perfil de médico e/ou admin!");
            attr.addFlashAttribute("usuario", usuario);
        } else {
            try {
                service.salvarUsuario(usuario);
                attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
            } catch (DataIntegrityViolationException ex) {
                attr.addFlashAttribute("falha", "Cadastro não realizado, email já existente.");
            }
        }
        return "redirect:/u/novo/cadastro/usuario";
    }

    @GetMapping("/editar/credenciais/usuario/{id}")
    public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
        return new ModelAndView("usuario/cadastro", "usuario", service.buscarPorId(id));
    }

    @GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
    public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,
                                                       @PathVariable("perfis") Long[] perfisId) {

        Usuario usuario = service.buscarPorIdEPerfis(usuarioId, perfisId);

        if (usuario.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod()))
                && !usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
            return new ModelAndView("usuario/cadastro", "usuario", usuario);

        } else if (usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
            Medico medico = medicoService.buscarPorUsuarioId(usuarioId);
            return medico.hasNotId()
                    ? new ModelAndView("medico/cadastro", "medico", new Medico(new Usuario(usuarioId)))
                    : new ModelAndView("medico/cadastro", "medico", medico);

        } else if (usuario.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
            ModelAndView model = new ModelAndView("error");
            model.addObject("status", 403);
            model.addObject("error", "Área Restrita");
            model.addObject("message", "Os dados do paciente só podem ser alterados pelo próprio paciente.");
            return model;
        }
        return new ModelAndView("redirect:/u/lista");
    }

    @GetMapping("/editar/senha")
    public String abrirEditarSenha() {
        return "usuario/editar-senha";
    }

    @PostMapping("/confirmar/senha")
    public String editarSenha(@RequestParam("senha1") String senha1,
                              @RequestParam("senha2") String senha2,
                              @RequestParam("senha3") String senha3,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes attr) {
        if (!senha1.equals(senha2)) {
            attr.addFlashAttribute("falha", "Senhas não conferem, tente novamente.");
            return "redirect:/u/editar/senha";
        }

        Usuario usuario = service.buscarPorEmail(user.getUsername());
        if (!service.isSenhaCorreta(senha3, usuario.getSenha())) {
            attr.addFlashAttribute("falha", "Senha atual não confere, tente novamente.");
            return "redirect:/u/editar/senha";
        }

        service.alterarSenha(usuario, senha1);
        attr.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        return "redirect:/u/editar/senha";
    }

    @GetMapping("/novo/cadastro")
    public String novoCadastro(Usuario usuario) {
        return "cadastrar-se";
    }

    @GetMapping("/cadastro/realizado")
    public String cadastroRealizado() {
        return "fragments/mensagem";
    }

    @PostMapping("/cadastro/paciente/salvar")
    public String salvarCadastroPaciente(Usuario usuario, BindingResult result) throws MessagingException {
        try {
            service.salvarCadastroPaciente(usuario);
        } catch (DataIntegrityViolationException ex) {
            result.reject("email", "Ops... este email já está cadastrado.");
            return "cadastrar-se";
        }
        return "redirect:/u/cadastro/realizado";
    }

    @GetMapping("/confirmacao/cadastro")
    public String respostaConfirmacaoCadastroPaciente(@RequestParam("codigo") String codigo,
                                                      RedirectAttributes attr) {
        service.ativarCadastroPaciente(codigo);
        attr.addFlashAttribute("alerta", "sucesso");
        attr.addFlashAttribute("titulo", "Cadastro ativado com sucesso.");
        attr.addFlashAttribute("texto", "Parabéns, seu cadastro está ativo.");
        attr.addFlashAttribute("subtexto", "Siga com o login para acessar o sistema.");
        return "redirect:/login";
    }

    @GetMapping("/p/redefinir/senha")
    public String pedidoRedefinirSenha() {
        return "usuario/pedido-recuperar-senha";
    }

    @GetMapping("/p/recuperar/senha")
    public String redefinirSenha(@RequestParam("email") String email, ModelMap model) throws MessagingException {
        service.pedidoRedefinicaoDeSenha(email);
        model.addAttribute("sucesso",
                "Um email foi enviado para " + email + " com as instruções para redefinir sua senha.");
        model.addAttribute("usuario", new Usuario(email));
        return "usuario/recuperar-senha";
    }

    @PostMapping("/p/nova/senha")
    public String confirmacaoDeRedefinicaoDeSenha(Usuario usuario, ModelMap model) {
        Usuario usuarioRecuperado = service.buscarPorEmail(usuario.getEmail());
        if (!usuarioRecuperado.getCodigoVerificador().equals(usuario.getCodigoVerificador())) {
            model.addAttribute("falha", "Código de verificação inválido.");
            return "usuario/recuperar-senha";
        }
        usuarioRecuperado.setCodigoVerificador(null);
        service.alterarSenha(usuarioRecuperado, usuario.getSenha());
        model.addAttribute("alerta", "Sucesso");
        model.addAttribute("titulo", "Senha redefinida.");
        model.addAttribute("texto", "Você já pode fazer login com sua nova senha.");
        return "login";
    }
}
