package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.List;

public abstract class MailCriterion {
	public abstract List<Mail> meetsCriterion(List<Mail> mailList);
}
