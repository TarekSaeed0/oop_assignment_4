package com.github.oop_assignment_4.repository;

import com.github.oop_assignment_4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Mail;

import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
	List<Mail> findByUserId(Long id);

	List<Mail> findByData_Sender_Id(Long dataSenderId);

	List<Mail> findByUserAndData_Sender(User user, User dataSender);
}
