package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.*;
import com.github.oop_assignment_4.exception.AttachmentNotFoundException;
import com.github.oop_assignment_4.facade.FilterFacade;
import com.github.oop_assignment_4.facade.MailDtoFacade;
import com.github.oop_assignment_4.model.*;
import com.github.oop_assignment_4.model.mailCriterion.*;
import com.github.oop_assignment_4.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
	@Autowired
	UserFolderRepository userFolderRepository;
	@Autowired
	private AttachmentRepository attachmentRepository;


	@Transactional
	public List<SentMailDTO> getSent(InboxRequest inboxRequest) {
		User sender = userRepository.findById(inboxRequest.getUserId())
				.orElseThrow(() -> new RuntimeException("cant find user"));
		List<Mail> senderCopy =
				mailRepository.findByUserAndData_Sender(sender, sender);

		List<Mail> filtered = FilterFacade.filterToSentBox(sender, senderCopy, inboxRequest.getFilterBy(),
				inboxRequest.getSearchBy(), inboxRequest.getPriority(),
				inboxRequest.isHasAttachment());

		// sorting
		MailComparatorFactory mcf = new MailComparatorFactory();
		filtered.sort(mcf.getComparator(inboxRequest.getSortBy()));

		List<SentMailDTO> inboxMailDTOS = MailDtoFacade.toSentDto(filtered);

		// pagination
		return inboxMailDTOS.subList(
				(inboxRequest.getPage() - 1) * inboxRequest.getSize(),
				Math.min(inboxMailDTOS.size(),
						(inboxRequest.getPage()) * inboxRequest.getSize()));
	}


	@Transactional
	public List<InboxMailDTO> getInbox(InboxRequest inboxRequest) {
		User receiver =
				userRepository.findById(inboxRequest.getUserId()).orElseThrow();
		List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());


		List<Mail> filtered = FilterFacade.filterToInbox(receiver, allMail, inboxRequest.getFilterBy(),
				inboxRequest.getSearchBy(), inboxRequest.getPriority(),
				inboxRequest.isHasAttachment());
		// sorting
		MailComparatorFactory mcf = new MailComparatorFactory();
		filtered.sort(mcf.getComparator(inboxRequest.getSortBy()));

		List<InboxMailDTO> inboxMailDTOS = MailDtoFacade.toInboxDTO(filtered);

		return inboxMailDTOS.subList(
				(inboxRequest.getPage() - 1) * inboxRequest.getSize(),
				Math.min(inboxMailDTOS.size(),
						(inboxRequest.getPage()) * inboxRequest.getSize()));
	}

	@Transactional
	public String isValidEmail(MailSendDto mailSendDto) {
		User test;
		for (String to : mailSendDto.getTo()) {
			test = (userRepository.findByEmail(to)
					.orElseThrow(() -> new RuntimeException(to)));
		}
		return "done";
	}

	@Transactional
	public String sendEmail(MailSendDto mailSendDto) {
		User sender = userRepository.findById(mailSendDto.getUserId())
				.orElseThrow(() -> new RuntimeException("not found"));
		UserDTO dto =
				new UserDTO(sender.getId(), sender.getEmail(), sender.getName());
		MailData mailData = MailData.builder().subject(mailSendDto.getSubject())
				.body(mailSendDto.getBody())
				.priority(Priority.valueOf(mailSendDto.getPriority())).sender(sender)
				.build();
		Set<User> receivers = new HashSet<>();
		for (String to : mailSendDto.getTo()) {
			receivers.add(userRepository.findByEmail(to)
					.orElseThrow(() -> new RuntimeException(to + " not found")));
		}
		System.out.println("receivers = " + receivers);
		Set<Attachment> attachments = mailSendDto.getAttachments().stream()
				.map((attachmentDTO) -> attachmentRepository
						.findById(attachmentDTO.getId()).orElseThrow(
								() -> new AttachmentNotFoundException(attachmentDTO.getId())))
				.collect(Collectors.toSet());
		mailData.setAttachments(attachments);
		mailData.setReceivers(receivers);
		mailData.setSentAt(LocalDateTime.now());
		mailDataRepository.save(mailData);
		List<Mail> mails = new ArrayList<>();
		// save for sender
		Mail mail = Mail.builder().user(sender).data(mailData).build();
		mails.add(mail);
		// save for receivers
		for (User receiver : receivers) {
			Mail receivedMail = Mail.builder().user(receiver).data(mailData).build();
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

		return MailDtoFacade.toInboxMailDto(mail);
	}

	@Transactional
	public SentMailDTO getSentEmail(Long id) {
		Mail mail = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));

		return MailDtoFacade.toSentMailDto(mail);
	}

	public void DeleteById(Long id) {
		Mail toBeDeleted = mailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("not found"));
		toBeDeleted.setDeletedAt(LocalDateTime.now());
		mailRepository.save(toBeDeleted);
	}

	public void deleteAllById(List<Long> ids) {
		List<Mail> toBeDeleted = mailRepository.findAllById(ids);
		for (Mail mail : toBeDeleted) {
			mail.setDeletedAt(LocalDateTime.now());
		}
		mailRepository.saveAll(toBeDeleted);
	}

	public List<MailResponse> getMailsByUserIdAndFolderName(Long userId,
			String folderName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		UserFolder folder = userFolderRepository.findByNameAndUser(folderName, user)
				.orElseThrow(() -> new RuntimeException("Folder not found"));

		List<Mail> mails =
				mailRepository.findByUserAndUserFolderAndDeletedAtNull(user, folder);

		return mails.stream().map(mail -> {
			MailDataResponse dataResponse =
					new MailDataResponse(mail.getData().getSender().getName(),
							mail.getData().getSender().getEmail(),
							mail.getData().getSubject(), mail.getData().getBody(),
							mail.getData().getSentAt(), mail.getData().getPriority());

			return new MailResponse(mail.getId(), dataResponse, folder.getName());
		}).collect(Collectors.toList());
	}
}
