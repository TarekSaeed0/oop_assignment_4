package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;

import java.util.ArrayList;
import java.util.List;

public class FilterBySubjectCriterion extends MailCriterion {
	private final String searchBy;

	public FilterBySubjectCriterion(String searchBy) {
		this.searchBy = searchBy.toLowerCase();
	}

	@Override
	public List<Mail> meetsCriterion(List<Mail> mailList) {
		List<Mail> filtered = new ArrayList<>();
		for(Mail mail: mailList) {
			if(mail.getData().getSubject().toLowerCase().contains(searchBy)) {
				filtered.add(mail);
			}
		}
		return filtered;
	}
}
