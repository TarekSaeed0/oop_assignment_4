package com.github.oop_assignment_4.model.mailCriterion;

import com.github.oop_assignment_4.model.Mail;
import java.util.ArrayList;
import java.util.List;

public class OrCriterion extends MailCriterion {
    private final MailCriterion criteria1;
    private final MailCriterion criteria2;

    public OrCriterion(MailCriterion criteria1, MailCriterion criteria2) {
        this.criteria1 = criteria1;
        this.criteria2 = criteria2;
    }

    @Override
    public List<Mail> meetsCriterion(List<Mail> mails) {
        List<Mail> firstCriteriaItems = criteria1.meetsCriterion(mails);
        List<Mail> secondCriteriaItems = criteria2.meetsCriterion(mails);

        for (Mail mail : secondCriteriaItems) {
            if (!firstCriteriaItems.contains(mail)) {
                firstCriteriaItems.add(mail);
            }
        }
        return firstCriteriaItems;
    }
}