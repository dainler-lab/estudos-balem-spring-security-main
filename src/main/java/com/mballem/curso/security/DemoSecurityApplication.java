package com.mballem.curso.security;

import com.mballem.curso.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class DemoSecurityApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoSecurityApplication.class, args);
	}

	/*@Autowired
	JavaMailSender mailSender;*/

	/*@Autowired
	EmailService emailService;*/

	@Override
	public void run(String... args) throws Exception {
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
		/*SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo("keystow.contato@gmail.com");
		sm.setText("Teste de envio de email");
		sm.setSubject("Teste de envio de email 1");
		mailSender.send(sm);*/

		//emailService.enviarPedidoDeConfirmacaoDeCadastro("keystow.contato@gmail.com", "123");
	}
}
