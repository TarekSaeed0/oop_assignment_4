package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.MailSendDto;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.dto.MailDto;
import com.github.oop_assignment_4.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class MailController {

	@Autowired
	MailService mailService;
	@PostMapping("send")
	public String sendEmail(@RequestBody MailSendDto mailSendDto) {
		return mailService.sendEmail(mailSendDto);
	}

	@PostMapping("inbox")
	List<MailDto> getInbox(@RequestBody InboxRequest inboxRequest) {
		return mailService.getInbox(inboxRequest);
	}
	@PostMapping("sent")
	List<MailDto> getSent(@RequestBody InboxRequest inboxRequest) {
		return mailService.getSent(inboxRequest);
	}
}
