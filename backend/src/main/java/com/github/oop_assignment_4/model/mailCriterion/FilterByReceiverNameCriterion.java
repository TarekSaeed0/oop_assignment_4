package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import java.util.ArrayList;
import java.util.List;

public class FilterByReceiverNameCriterion extends MailCriterion {
    private final String searchBy;

    public FilterByReceiverNameCriterion(String searchBy) {
        this.searchBy = searchBy.toLowerCase();
    }

    @Override
    public List<Mail> meetsCriterion(List<Mail> mailList) {
        List<Mail> filtered = new ArrayList<>();

        for(Mail mail: mailList) {
            if (mail.getData().getReceivers() != null) {
                for (User receiver : mail.getData().getReceivers()) {
                    if (receiver.getName().toLowerCase().contains(searchBy)) {
                        filtered.add(mail);
                        break;
                    }
                }
            }
        }
        return filtered;
    }
}