package com.github.oop_assignment_4.repository;

import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.model.UserFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Mail;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
	List<Mail> findByUserId(Long id);

	List<Mail> findByData_Sender_Id(Long dataSenderId);

	List<Mail> findByUserAndData_Sender(User user, User dataSender);

	List<Mail> findByUserAndUserFolderAndDeletedAtNull(User user, UserFolder folder);


	List<Mail> findByUserIdAndDeletedAtNotNull(Long id);

	void deleteMailById(Long id);

	void deleteByDeletedAtBefore(LocalDateTime expiryDate);

}
