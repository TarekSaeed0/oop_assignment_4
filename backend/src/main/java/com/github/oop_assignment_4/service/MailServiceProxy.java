package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.InboxMailDTO;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.repository.MailRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Primary
public class MailServiceProxy extends MailService{

	@Autowired
	MailRepository mailRepository;
	@Override
	@Transactional
	public List<InboxMailDTO> getInbox(InboxRequest inboxRequest){
		List<Mail> deletedMail = mailRepository.findByUserIdAndDeletedAtNotNull(inboxRequest.getUserId());
		LocalDateTime limit = LocalDateTime.now().minusMinutes(3);
		for(Mail mail: deletedMail) {
			if(mail.getDeletedAt().isBefore(limit)){
				System.out.println("deleted" + mail.getId());
				mailRepository.deleteMailById(mail.getId());
			}
		}
		return super.getInbox(inboxRequest);
	}
}
