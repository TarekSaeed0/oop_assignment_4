package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.MailData;

import java.util.ArrayList;
import java.util.List;

public class GeneralSearchCriterionForSent extends MailCriterion{
	private final String searchBy;
	public GeneralSearchCriterionForSent(String searchBy) {
		this.searchBy = searchBy.toLowerCase();
	}
	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail> filtered = new ArrayList<>();
		for(Mail mail: mailList) {
			MailData mailData = mail.getData();
			if(
					mailData.getSender().getName().toLowerCase().contains(searchBy) ||
							mailData.getSender().getEmail().toLowerCase().contains(searchBy) ||
							mailData.getBody().toLowerCase().contains(searchBy) ||
							mailData.getSubject().toLowerCase().contains(searchBy)

			) {
				filtered.add(mail);
			}
		}
		return filtered;
	}
}
