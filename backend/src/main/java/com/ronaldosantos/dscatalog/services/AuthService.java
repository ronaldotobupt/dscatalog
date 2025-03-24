package com.ronaldosantos.dscatalog.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ronaldosantos.dscatalog.dto.EmailDTO;
import com.ronaldosantos.dscatalog.entities.PasswordRecover;
import com.ronaldosantos.dscatalog.entities.User;
import com.ronaldosantos.dscatalog.repositories.PasswordRecoverRepository;
import com.ronaldosantos.dscatalog.repositories.UserRepository;
import com.ronaldosantos.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class AuthService {
	
	@Value("${email.password-recover.token.minutes}")
	private Long tokenMinutes;
	
	@Value("${email.password-recover.uri}")
	private String recoverUri;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordRecoverRepository passwordRecoverRepository;
	
	@Autowired
	private EmailService emailService;

	
	
	
	@Transactional
	public void createRecoverToken(EmailDTO body) {
		
		User user = userRepository.findByEmail(body.getEmail());
		if(user == null) {
			throw new ResourceNotFoundException("Email não encontrado");
		}
		
		String token = (UUID.randomUUID().toString());
		
		PasswordRecover entity = new PasswordRecover();
		entity.setEmail(body.getEmail());
		entity.setToken(token);
		entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
		
		entity = passwordRecoverRepository.save(entity);
		
		String text = "Acesse o link para definir uma nova senha \n\n"+
		recoverUri + token;
		
		emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
		
	}

}
