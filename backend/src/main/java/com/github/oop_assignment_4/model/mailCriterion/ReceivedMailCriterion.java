package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import lombok.Getter;

import java.util.List;
@Getter
public class ReceivedMailCriterion extends MailCriterion {

	User receiver;
	public ReceivedMailCriterion(User receiver) {
		this.receiver = receiver;
	}
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		return mailList.stream().filter((m) -> m.getData().getSender() != receiver).toList();
	}
}
