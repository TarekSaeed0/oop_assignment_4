package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import lombok.Getter;

import java.util.List;
@Getter
public class SentMailCriterion extends MailCriterion {

	User sender;
	public SentMailCriterion(User sender) {
		this.sender = sender;
	}
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		return mailList.stream().filter((m) -> m.getData().getSender() == sender).toList();
	}
}
