package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.List;

public class DeletedCriterion extends MailCriterion{
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {

		return mailList.stream().filter((m) -> m.getDeletedAt() != null).toList();
	}
}
