package com.github.oop_assignment_4.repository;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailRepositoryTest {
	@Autowired
	private MailRepository mailRepository;
	@Autowired
	UserRepository userRepository;

	@Test
	public void getInbox() {
//		List<Mail> mail = mailRepository.findByUserId(1L);
		User omar = userRepository.findById(1L)
				.orElseThrow(() -> new RuntimeException("User not found"));
		System.out.println("omar = " + omar);

	}
}