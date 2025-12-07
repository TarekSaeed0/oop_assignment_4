package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.MailSendDto;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.dto.MailDto;
import com.github.oop_assignment_4.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
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
	@PostMapping("trash")
	List<MailDto> getTrash(@RequestBody InboxRequest inboxRequest) {
		return mailService.getSent(inboxRequest);
	}
	@GetMapping("getEmail/{id}")
	MailDto getEmail(@PathVariable Long id) {
		return mailService.getEmail(id);
	}
	@DeleteMapping("deleteEmail/{id}")
	String deleteEmail(@PathVariable Long id) {
		System.out.println(id);
		return mailService.DeleteById(id);
	}
	@PutMapping("bulkDelete")
	public void deleteAllById(@RequestBody List<Long> ids) {
		mailService.deleteAllById(ids);
	}
}
