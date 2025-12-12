package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.dto.SignupRequest;
import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {
	@Autowired
	MailService mailService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private AuthenticationService authenticationService;

	@Test
	public void register() {
		SignupRequest req = new SignupRequest();
		req.setEmail("omar@gmail.com");
		req.setName("omar");
		req.setPassword("asdasd");
		authenticationService.signup(req);
	}

}