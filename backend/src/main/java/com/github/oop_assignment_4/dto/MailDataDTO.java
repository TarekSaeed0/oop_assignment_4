package com.github.oop_assignment_4.dto;

import com.github.oop_assignment_4.model.Attachment;
import com.github.oop_assignment_4.model.Priority;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class MailDataDTO {
	private SenderDTO sender;
	private String subject;
	private String body;
	private Priority priority;
	private Set<Attachment> attachments;
	private LocalDateTime sentAt;
}
