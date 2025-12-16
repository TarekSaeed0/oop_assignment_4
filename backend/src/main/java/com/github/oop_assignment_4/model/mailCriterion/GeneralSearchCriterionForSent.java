package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.MailData;
import com.github.oop_assignment_4.model.User;

import java.util.ArrayList;
import java.util.List;

public class GeneralSearchCriterionForSent extends MailCriterion {
	private final String searchBy;

	public GeneralSearchCriterionForSent(String searchBy) {
		this.searchBy = searchBy.toLowerCase();
	}

	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail> filtered = new ArrayList<>();
		for (Mail mail : mailList) {
			MailData mailData = mail.getData();

			boolean matchesBodyOrSubject =
					mailData.getBody().toLowerCase().contains(searchBy) ||
							mailData.getSubject().toLowerCase().contains(searchBy);

			boolean matchesReceiver = false;
			if (mailData.getReceivers() != null) {
				for (User receiver : mailData.getReceivers()) {
					if (receiver.getName().toLowerCase().contains(searchBy) ||
							receiver.getEmail().toLowerCase().contains(searchBy)) {
						matchesReceiver = true;
						break;
					}
				}
			}
			if (matchesBodyOrSubject || matchesReceiver) {
				filtered.add(mail);}
		}
		return filtered;
	}
}