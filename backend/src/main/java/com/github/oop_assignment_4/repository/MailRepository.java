package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Mail;

import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
	List<Mail> findByUserId(Long id);
}
