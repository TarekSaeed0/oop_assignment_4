package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.EmailDto;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.model.ReceivedMail;
import com.github.oop_assignment_4.model.SentMail;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.repository.DraftRepository;
import com.github.oop_assignment_4.repository.ReceivedMailRepository;
import com.github.oop_assignment_4.repository.SentMailRepository;
import com.github.oop_assignment_4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MailService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DraftRepository draftRepository;
	@Autowired
	private SentMailRepository sentMailRepository;
	@Autowired
	private ReceivedMailRepository receivedMailRepository;

	List<ReceivedMail> getInbox() {
		return List.of(null);
	}

	public String sendEmail(EmailDto emailDto) {

		try {
			User sender = userRepository.findById(emailDto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));
			SentMail sentMail = SentMail.builder()
					.sender(sender)
					.subject(emailDto.getSubject())
					.body(emailDto.getBody())
					.priority(emailDto.getPriority())
					.sentAt(LocalDateTime.now())
					.build();
			sender.getSentMails().add(sentMail);
			Set<ReceivedMail> receivedMails = new HashSet<>();
			List<User> recipients = new ArrayList<>();
			for(String email: emailDto.getTo()) {
				User recipient =  userRepository.findByEmail(email)
						.orElseThrow(() -> new RuntimeException("email: " + email + " is not found"));
				recipients.add(recipient);
				ReceivedMail receivedMail = ReceivedMail.builder()
						.sentMail(sentMail)
						.receiver(recipient)
						.body(sentMail.getBody())
						.subject(sentMail.getSubject())
						.priority(sentMail.getPriority())
						.senderEmail(sender.getEmail())
						.build();
				recipient.getReceivedMails().add(receivedMail);
				receivedMails.add(receivedMail);
				recipient.getReceivedMails().add(receivedMail);
			}
			sentMail.setReceivedMails(receivedMails);


			//save the sender
			sentMailRepository.save(sentMail);
			userRepository.save(sender);
			userRepository.saveAll(recipients);



		} catch (RuntimeException e) {
			return e.getMessage();
		}


		return "sent";
	}

	public Collection<ReceivedMail> getInbox(InboxRequest inboxRequest) {
		System.out.println(inboxRequest);
		try {
			Long id = (inboxRequest.getUserId() == null? 2: inboxRequest.getUserId());
//			User user = userRepository.findById(id)
//					.orElseThrow(() -> new RuntimeException("User not found"));
			return receivedMailRepository.getReceivedMailByReceiverId(2L);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}

	}

}
