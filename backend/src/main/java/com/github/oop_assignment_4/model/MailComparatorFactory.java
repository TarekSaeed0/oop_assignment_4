package com.github.oop_assignment_4.model;

import com.github.oop_assignment_4.dto.InboxMailDTO;


import java.util.Comparator;

public class MailComparatorFactory {
	public Comparator<Mail> getComparator(String sortBy) {
		switch (sortBy) {
			case "sender" -> {
				return Comparator.comparing((a) ->a.getData().getSender().getName());
			}
			case "receiver" -> {
				return Comparator.comparing((a) ->a.getUser().getName());
			}
			case "body" -> {
				return Comparator.comparing((a) ->a.getData().getBody());
			}
			case "subject" -> {
				return Comparator.comparing((a) ->a.getData().getSubject());
			}
			case "priority" -> {
				return Comparator.comparing((a) ->a.getData().getPriority());
			}
			case "date"-> {
				return Comparator.comparing((a) -> -a.getData().getSentAt().getSecond());
			}
			default -> {
				return Comparator.comparing(a -> a.getData().getSentAt());
			}
		}
	}
}
