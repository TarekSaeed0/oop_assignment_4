package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.MailSendDto;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.dto.InboxMailDTO;
import com.github.oop_assignment_4.dto.SentMailDTO;
import com.github.oop_assignment_4.service.MailService;
import com.github.oop_assignment_4.service.MailServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class MailController {

	MailService mailService;
	@Autowired
	MailController(MailServiceProxy mailService) {
		this.mailService = mailService;
	}
	@PostMapping("send")
	public String sendEmail(@RequestBody MailSendDto mailSendDto) {
		return mailService.sendEmail(mailSendDto);
	}

	@PostMapping("inbox")
	List<InboxMailDTO> getInbox(@RequestBody InboxRequest inboxRequest) {
		return mailService.getInbox(inboxRequest);
	}
	@PostMapping("sent")
	List<SentMailDTO> getSent(@RequestBody InboxRequest inboxRequest) {
		return mailService.getSentV2(inboxRequest);
	}
	/*@PostMapping("trash")
	List<InboxMailDTO> getTrash(@RequestBody InboxRequest inboxRequest) {
		return mailService.getSent(inboxRequest);
	}*/
	@GetMapping("getInboxEmail/{id}")
	InboxMailDTO getInboxEmail(@PathVariable Long id) {
		return mailService.getInboxEmail(id);
	}
	@GetMapping("getSentEmail/{id}")
	SentMailDTO getSentEmail(@PathVariable Long id) {
		return mailService.getSentEmail(id);
	}
	@DeleteMapping("deleteEmail/{id}")
	void deleteEmail(@PathVariable Long id) {
		mailService.DeleteById(id);
	}
	@PutMapping("bulkDelete")
	public void deleteAllById(@RequestBody List<Long> ids) {
		mailService.deleteAllById(ids);
	}
}
