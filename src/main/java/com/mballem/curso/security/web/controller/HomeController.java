package com.mballem.curso.security.web.controller;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado(ModelMap model, HttpServletResponse response) {
        model.addAttribute("status", response.getStatus());
        model.addAttribute("error", "Acesso Negado.");
        model.addAttribute("message", "Você não tem permissão para acessar esta área ou ação.");
        return "error";
    }

    @GetMapping("/login-error")
    public String loginError(ModelMap model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String lastException = String.valueOf(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));

        if (lastException.contains(SessionAuthenticationException.class.getName())) {
            model.addAttribute("alerta", "erro");
            model.addAttribute("titulo", "Acesso recusado.");
            model.addAttribute("texto", "Você já está logado em outro dispositivo.");
            model.addAttribute("subtexto", "Faça o logout do dispositivo anterior ou espere sua sessão expirar.");
            return "login";
        }

        model.addAttribute("alerta", "erro");
        model.addAttribute("titulo", "Credenciais inválidas.");
        model.addAttribute("texto", "Login ou senha incorretos, tente novamente.");
        model.addAttribute("subtexto", "Acesso permitido somente para cadastros ativados.");
        return "login";
    }

    @GetMapping("/expired")
    public String sessionExpired(ModelMap model) {
        model.addAttribute("alerta", "erro");
        model.addAttribute("titulo", "Acesso recusado.");
        model.addAttribute("texto", "Sua sessão expirou.");
        model.addAttribute("subtexto", "Você logou em outro dispositivo.");
        return "login";
    }
}
