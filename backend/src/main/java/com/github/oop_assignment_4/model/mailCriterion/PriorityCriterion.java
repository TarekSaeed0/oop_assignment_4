package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.Priority;

import java.util.ArrayList;
import java.util.List;

public class PriorityCriterion extends MailCriterion{
	Priority priority;
	public PriorityCriterion(String priority) {
		this.priority = Priority.valueOf(priority);
	}
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail> filtered = new ArrayList<>();
		for(Mail mail: mailList) {
			if(mail.getData().getPriority() == priority) {
				filtered.add(mail);
			}
		}
		return filtered;
	}
}
