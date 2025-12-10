package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.ArrayList;
import java.util.List;

public class AndCriterion extends MailCriterion {
	MailCriterion mailCriterion1;
	MailCriterion mailCriterion2;
	public AndCriterion(MailCriterion mailCriterion1, MailCriterion mailCriterion2) {
		this.mailCriterion1 = mailCriterion1;
		this.mailCriterion2 = mailCriterion2;
	}
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail> meets1 = mailCriterion1.meetsCriterion(mailList);
		List<Mail> meets2 = mailCriterion2.meetsCriterion(mailList);
		List<Mail> meetsBoth = new ArrayList<>();
		for(Mail mail: meets1) {
			if (meets2.contains(mail)) meetsBoth.add(mail);
		}

		return meetsBoth;
	}
}
