package com.github.oop_assignment_4.dto;

import com.github.oop_assignment_4.model.Attachment;
import com.github.oop_assignment_4.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
public class SentMailDataDTO {
	private SenderDTO sender;
	private List<SenderDTO> receivers;
	private String subject;
	private String body;
	private Priority priority;
	private Set<Attachment> attachments;
	private LocalDateTime sentAt;
}
