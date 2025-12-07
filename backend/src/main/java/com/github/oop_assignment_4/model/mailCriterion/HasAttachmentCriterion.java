package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.ArrayList;
import java.util.List;

public class HasAttachmentCriterion extends MailCriterion{
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail>  filtered = new ArrayList<>();
		for(Mail mail: mailList) {
			if(!mail.getData().getAttachments().isEmpty()) {
				filtered.add(mail);
			}
		}
		return filtered;
	}
}
