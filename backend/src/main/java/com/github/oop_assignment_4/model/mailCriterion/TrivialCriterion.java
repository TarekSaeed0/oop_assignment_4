package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.List;

public class TrivialCriterion extends MailCriterion{
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		return mailList;
	}
}
