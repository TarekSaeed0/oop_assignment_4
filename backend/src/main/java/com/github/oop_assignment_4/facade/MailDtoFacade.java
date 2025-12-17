package com.github.oop_assignment_4.facade;

import com.github.oop_assignment_4.dto.*;
import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.MailData;
import com.github.oop_assignment_4.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MailDtoFacade {

	public static List<InboxMailDTO> toInboxDTO(List<Mail> receivedMail) {
		List<InboxMailDTO> inboxMailDTOS = new ArrayList<>();
		for (Mail mail : receivedMail) {
			inboxMailDTOS.add(MailDtoFacade.toInboxMailDto(mail));
		}
		return inboxMailDTOS;
	}

	public static List<SentMailDTO> toSentDto(List<Mail> receivedMail) {
		List<SentMailDTO> inboxMailDTOS = new ArrayList<>();
		for (Mail mail : receivedMail) {
			inboxMailDTOS.add(MailDtoFacade.toSentMailDto(mail));
		}
		return inboxMailDTOS;
	}

	public static InboxMailDTO toInboxMailDto(Mail mail) {
		User receiver = mail.getUser();
		MailData mailData = mail.getData();
		User sender = mail.getData().getSender();
		SenderDTO receiverDto = SenderDTO.builder().email(receiver.getEmail())
				.name(receiver.getName()).build();
		SenderDTO senderDTO = SenderDTO.builder().email(sender.getEmail())
				.name(sender.getName()).build();
		ReceivedMailDataDTO receivedMailDataDTO =
				ReceivedMailDataDTO.builder().receiver(receiverDto).sender(senderDTO)
						.sentAt(mailData.getSentAt()).subject(mailData.getSubject())
						.body(mailData.getBody()).attachments(mailData.getAttachments())
						.priority(mailData.getPriority()).build();
		return InboxMailDTO.builder().data(receivedMailDataDTO).id(mail.getId())
				.build();
	}


	public static SentMailDTO toSentMailDto(Mail mail) {
		User sender = mail.getUser();
		MailData mailData = mail.getData();

		Set<Mail> allMails = mailData.getMails();
		List<SenderDTO> receiversDTOs = new ArrayList<>();
		for (Mail copy : allMails) {
			if (copy == mail)
				continue;
			User receiverUser = copy.getUser();
			SenderDTO receiverDTO = SenderDTO.builder().name(receiverUser.getName())
					.email(receiverUser.getEmail()).build();
			receiversDTOs.add(receiverDTO);
		}

		SenderDTO senderDTO = SenderDTO.builder().email(sender.getEmail())
				.name(sender.getName()).build();
		SentMailDataDTO sentMailDataDTO =
				SentMailDataDTO.builder().receivers(receiversDTOs).sender(senderDTO)
						.sentAt(mailData.getSentAt()).subject(mailData.getSubject())
						.body(mailData.getBody()).attachments(mailData.getAttachments())
						.priority(mailData.getPriority()).build();
		return SentMailDTO.builder().id(mail.getId()).data(sentMailDataDTO).build();
	}
}
