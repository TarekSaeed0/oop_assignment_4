package com.github.oop_assignment_4.facade;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.model.mailCriterion.*;

import java.util.List;
import java.util.Objects;

public class FilterFacade {

	FilterFacade instance;
	private FilterFacade() {

	}

	FilterFacade getInstance() {
		if(instance == null) {
			instance = new FilterFacade();
		}
		return instance;
	}

	static List<Mail> filter(List<Mail> mailList, String filterBy,
					  String searchBy, String priority, boolean hasAttachment) {
		MailCriterion criterion = new TrivialCriterion();
		if (filterBy.equals("body")) {
			criterion =
					new AndCriterion(criterion, new FilterByBodyCriterion(searchBy));
		}
		if (filterBy.equals("subject")) {
			criterion =
					new AndCriterion(criterion, new FilterBySubjectCriterion(searchBy));
		}
		if (filterBy.equals("name")) {
			criterion = new AndCriterion(criterion,
					new FilterBySenderNameCriterion(searchBy));
		}
		if (filterBy.equals("email")) {
			criterion = new AndCriterion(criterion,
					new FilterBySenderEmailCriterion(searchBy));
		}
		if (hasAttachment) {
			criterion = new AndCriterion(criterion, new HasAttachmentCriterion());
		}
		if (!Objects.equals(priority, "any")) {
			criterion = new AndCriterion(criterion, new PriorityCriterion(priority));
		}
		return criterion.meetsCriterion(mailList);
	}

	public static List<Mail> filterToInbox(User receiver, List<Mail> mailList, String filterBy,
					  String searchBy, String priority, boolean hasAttachment) {
		MailCriterion receivedMailCriterion = new AndCriterion(
				new NotDeletedCriterion(), new ReceivedMailCriterion(receiver));

		List<Mail> mail = new AndCriterion(
				new GeneralSearchCriterionForInbox(searchBy),
				receivedMailCriterion
				).meetsCriterion(mailList);
		return filter(mail, filterBy, searchBy, priority, hasAttachment);
	}

	public static List<Mail> filterToSentBox(User sender, List<Mail> mailList, String filterBy,
											 String searchBy, String priority, boolean hasAttachment) {
		MailCriterion sentCriterion = new AndCriterion(
				new SentMailCriterion(sender), new NotDeletedCriterion());
		List<Mail> mail = new AndCriterion(
				sentCriterion,
				new GeneralSearchCriterionForSent(searchBy)
				).meetsCriterion(mailList);
		return filter(mail, filterBy, searchBy, priority, hasAttachment);
	}
}
