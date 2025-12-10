package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.*;

import com.github.oop_assignment_4.model.*;
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


	public InboxMailDTO toInboxMailDto(Mail mail) {
		User receiver = mail.getUser();
		MailData mailData = mail.getData();
		User sender = mail.getData().getSender();
		SenderDTO receiverDto = SenderDTO.builder()
				.email(receiver.getEmail())
				.name(receiver.getName())
				.build();
		SenderDTO senderDTO = SenderDTO.builder()
				.email(sender.getEmail())
				.name(sender.getName())
				.build();
		ReceivedMailDataDTO receivedMailDataDTO = ReceivedMailDataDTO.builder()
				.receiver(receiverDto)
				.sender(senderDTO)
				.sentAt(mailData.getSentAt())
				.subject(mailData.getSubject())
				.body(mailData.getBody())
				.attachments(mailData.getAttachments())
				.priority(mailData.getPriority())
				.build();
		return InboxMailDTO.builder()
				.data(receivedMailDataDTO)
				.id(mail.getId())
				.build();
	}

	public List<InboxMailDTO> toInboxDTO(List<Mail> receivedMail) {
		List<InboxMailDTO> inboxMailDTOS = new ArrayList<>();
		for (Mail mail: receivedMail) {
			inboxMailDTOS.add(toInboxMailDto(mail));
		}
		return inboxMailDTOS;
	}
	public List<SentMailDTO> toSentDto(List<Mail> receivedMail) {
		List<SentMailDTO> inboxMailDTOS = new ArrayList<>();
		for (Mail mail: receivedMail) {
			inboxMailDTOS.add(toSentMailDto(mail));
		}
		return inboxMailDTOS;
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
	public List<InboxMailDTO> getSent(InboxRequest inboxRequest) {
		User sender = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> all=mailRepository.findByData_Sender_Id(inboxRequest.getUserId());

		MailCriterion sentCriterion = new SentMailCriterion(sender);
		List<Mail> sent = sentCriterion.meetsCriterion(all);

		List<Mail> filtered = filter(false, sent, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());

		List<InboxMailDTO> inboxMailDTOS = toInboxDTO(filtered);


		return inboxMailDTOS.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(inboxMailDTOS.size() ,
						(inboxRequest.getPage())* inboxRequest.getSize()
				)
		);
	}
	@Transactional
	public List<SentMailDTO> getSentV2(InboxRequest inboxRequest) {
		User sender = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> senderCopy =mailRepository.findByUserAndData_Sender(sender, sender);
		MailCriterion sentCriterion = new AndCriterion(new SentMailCriterion(sender), new NotDeletedCriterion()) ;
		List<Mail> sent = sentCriterion.meetsCriterion(senderCopy);

		List<Mail> filtered = filter(false, sent, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());
		//todo: sort

		List<SentMailDTO> inboxMailDTOS = toSentDto(filtered);


		return inboxMailDTOS.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(inboxMailDTOS.size() ,
						(inboxRequest.getPage())* inboxRequest.getSize()
				)
		);
	}
	public SentMailDTO toSentMailDto(Mail mail) {
		User sender = mail.getUser();
		MailData mailData = mail.getData();

		Set<Mail> allMails = mailData.getMails();
		List<SenderDTO> receiversDTOs = new ArrayList<>();
		for(Mail copy: allMails) {
			if(copy == mail) continue;
			User receiverUser = copy.getUser();
			SenderDTO receiverDTO = SenderDTO.builder()
					.name(receiverUser.getName())
					.email(receiverUser.getEmail())
					.build();
			receiversDTOs.add(receiverDTO);
		}

		SenderDTO senderDTO = SenderDTO.builder()
				.email(sender.getEmail())
				.name(sender.getName())
				.build();
		SentMailDataDTO sentMailDataDTO = SentMailDataDTO.builder()
				.receivers(receiversDTOs)
				.sender(senderDTO)
				.sentAt(mailData.getSentAt())
				.subject(mailData.getSubject())
				.body(mailData.getBody())
				.attachments(mailData.getAttachments())
				.priority(mailData.getPriority())
				.build();
		return SentMailDTO.builder()
				.id(mail.getId())
				.data(sentMailDataDTO)
				.build();
	}

	@Transactional
	public List<InboxMailDTO> getInbox(InboxRequest inboxRequest) {
		User receiver = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

		MailCriterion receivedMailCriterion = new AndCriterion(new NotDeletedCriterion(), new ReceivedMailCriterion(receiver));

		List<Mail> received = receivedMailCriterion.meetsCriterion(allMail);
		List<Mail> filtered = filter(true, received, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());
		// sorting
		MailComparatorFactory mcf = new MailComparatorFactory();
		filtered.sort(mcf.getComparator(inboxRequest.getSortBy()));

		List<InboxMailDTO> inboxMailDTOS = toInboxDTO(filtered);

		return inboxMailDTOS.subList((inboxRequest.getPage() - 1)* inboxRequest.getSize() ,
				Math.min(inboxMailDTOS.size() ,
						(inboxRequest.getPage())* inboxRequest.getSize()
				)
		);
	}
	@Transactional
	public List<InboxMailDTO> getTrash(InboxRequest inboxRequest) {
		User receiver = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

		MailCriterion receivedMailCriterion = new AndCriterion(new DeletedCriterion(), new ReceivedMailCriterion(receiver));

		List<Mail> received = receivedMailCriterion.meetsCriterion(allMail);

		List<Mail> filtered = filter(true, received, inboxRequest.getFilterBy(), inboxRequest.getSearchBy(),
				inboxRequest.getPriority(), inboxRequest.isHasAttachment());
//		List<Mail> sorted = Arrays.sort(filtered, new <Mail>());

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
	@Transactional
	public InboxMailDTO getInboxEmail(Long id) {
		Mail mail = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));

		return toInboxMailDto(mail);
	}
	@Transactional
	public SentMailDTO getSentEmail(Long id) {
		Mail mail = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));

		return toSentMailDto(mail);
	}

	public void DeleteById(Long id) {
		Mail toBeDeleted = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));
		toBeDeleted.setDeletedAt(LocalDateTime.now());
		mailRepository.save(toBeDeleted);
	}
	public void deleteAllById(List<Long> ids) {
		List<Mail> toBeDeleted = mailRepository.findAllById(ids);
		for(Mail mail: toBeDeleted) {
			mail.setDeletedAt(LocalDateTime.now());
		}
		mailRepository.saveAll(toBeDeleted);
	}


}
