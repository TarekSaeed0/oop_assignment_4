package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.*;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.MailData;
import com.github.oop_assignment_4.model.Priority;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.model.mailCriterion.*;
import com.github.oop_assignment_4.repository.DraftRepository;
import com.github.oop_assignment_4.repository.MailDataRepository;
import com.github.oop_assignment_4.repository.MailRepository;
import com.github.oop_assignment_4.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MailService {
	@Autowired
	private MailRepository mailRepository;
	@Autowired
	private DraftRepository draftRepository;
	@Autowired
	private MailDataRepository mailDataRepository;
	@Autowired
	private UserRepository userRepository;


	public List<MailDto> toInboxDTO(List<Mail> receivedMail) {
		List<MailDto> mailDTOs = new ArrayList<>();
		for (Mail mail: receivedMail) {
			User sender = mail.getData().getSender();
			MailData mailData = mail.getData();
			SenderDTO senderDTO = SenderDTO.builder()
					.email(sender.getEmail())
					.name(sender.getName())
					.build();
			MailDataDTO mailDataDTO = MailDataDTO.builder()
					.sender(senderDTO)
					.sentAt(mailData.getSentAt())
					.subject(mailData.getSubject())
					.body(mailData.getBody())
					.attachments(mailData.getAttachments())
					.priority(mailData.getPriority())
					.build();
			MailDto mailDto = MailDto.builder()
					.data(mailDataDTO)
					.id(mail.getId())
					.build();
			mailDTOs.add(mailDto);
		}
		return mailDTOs;
	}
	List<Mail> filter(boolean inbox, List<Mail> mailList, String filterBy, String searchBy, String priority, boolean hasAttachment) {
		MailCriterion criterion = (inbox? new GeneralSearchCriterionForInbox(searchBy):
				new GeneralSearchCriterionForSent(searchBy));
		if(filterBy.equals("body")) {
			criterion = new AndCriterion(criterion, new FilterByBodyCriterion(searchBy));
		}
		if(filterBy.equals("subject")) {
			criterion = new AndCriterion(criterion, new FilterBySubjectCriterion(searchBy));
		}
		if(filterBy.equals("name")) {
			criterion = new AndCriterion(criterion, new FilterBySenderNameCriterion(searchBy));
		}
		if(filterBy.equals("email")) {
			criterion = new AndCriterion(criterion, new FilterBySenderEmailCriterion(searchBy));
		}
		if(hasAttachment) {
			criterion = new AndCriterion(criterion, new HasAttachmentCriterion());
		}
		if(!Objects.equals(priority, "any")) {
			criterion = new AndCriterion(criterion, new PriorityCriterion(priority));
		}
		return criterion.meetsCriterion(mailList);
	}
	@Transactional
	public List<MailDto> getSent(InboxRequest inboxRequest) {
		User receiver = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

		MailCriterion sentMailCriterion = new SentMailCriterion(receiver);

		List<Mail> received = sentMailCriterion.meetsCriterion(allMail);

		List<Mail> filtered = filter(false, received, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());


		List<Mail> paged = filtered.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(filtered.size() ,
						(inboxRequest.getPage() - 1)* inboxRequest.getSize() + inboxRequest.getSize()
				)
		);
		return toInboxDTO(paged);
	}

	@Transactional
	public List<MailDto> getInbox(InboxRequest inboxRequest) {
		User receiver = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

		MailCriterion receivedMailCriterion = new AndCriterion(new NotDeletedCriterion(), new ReceivedMailCriterion(receiver));

		List<Mail> received = receivedMailCriterion.meetsCriterion(allMail);

		List<Mail> filtered = filter(true, received, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());


		List<Mail> paged = filtered.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(filtered.size() ,
						(inboxRequest.getPage() - 1)* inboxRequest.getSize() + inboxRequest.getSize()
				)
		);
		return toInboxDTO(paged);
	}
	@Transactional
	public List<MailDto> getTrash(InboxRequest inboxRequest) {
		User receiver = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

		MailCriterion receivedMailCriterion = new AndCriterion(new DeletedCriterion(), new ReceivedMailCriterion(receiver));

		List<Mail> received = receivedMailCriterion.meetsCriterion(allMail);

		List<Mail> filtered = filter(true, received, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());


		List<Mail> paged = filtered.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(filtered.size() ,
						(inboxRequest.getPage() - 1)* inboxRequest.getSize() + inboxRequest.getSize()
				)
		);
		return toInboxDTO(paged);
	}
	@Transactional
	public String sendEmail(MailSendDto mailSendDto) {
		User sender = userRepository.findById(mailSendDto.getUserId())
				.orElseThrow(()->new RuntimeException("not found"));
		UserDTO dto = new UserDTO(sender.getId(), sender.getEmail(), sender.getName());
		MailData mailData = MailData.builder()
				.subject(mailSendDto.getSubject())
				.body(mailSendDto.getBody())
				.priority(Priority.valueOf(mailSendDto.getPriority()))
				.sender(sender)
				.build();
		Set<User> receivers = new HashSet<>();
		for(String to: mailSendDto.getTo()) {
			receivers.add(userRepository.findByEmail(to)
					.orElseThrow(()-> new RuntimeException(to + " not found")));
		}
		System.out.println("receivers = " + receivers);
		// todo: add attachments
		mailData.setReceivers(receivers);
		mailData.setSentAt(LocalDateTime.now());
		mailDataRepository.save(mailData);
		List<Mail> mails = new ArrayList<>();
		// save for sender
		Mail mail = Mail.builder()
				.user(sender)
				.data(mailData)
				.build();
		mails.add(mail);
		// save for receivers
		for (User receiver: receivers) {
			Mail receivedMail = Mail.builder()
					.user(receiver)
					.data(mailData)
					.build();
			mails.add(receivedMail);
		}
		// save all
		mailRepository.saveAll(mails);
		return "sent";
	}
	public MailDto getEmail(Long id) {
		Mail mail = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));
		return toInboxDTO(List.of(mail)).getFirst();
	}

	public String DeleteById(Long id) {
		Mail toBeDeleted = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));
		toBeDeleted.setDeletedAt(LocalDateTime.now());
		mailRepository.save(toBeDeleted);
		return "Deleted";
	}


}
