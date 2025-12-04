package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.EmailDto;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.model.ReceivedMail;
import com.github.oop_assignment_4.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController

public class MailController {

	@Autowired
	MailService mailService;
	@PostMapping("send")
	public String sendEmail(@RequestBody EmailDto emailDto) {
		return mailService.sendEmail(emailDto);
	}

	@PostMapping("inbox")
	Collection<ReceivedMail> getInbox(InboxRequest inboxRequest) {
		return mailService.getInbox(inboxRequest);
	}
}
