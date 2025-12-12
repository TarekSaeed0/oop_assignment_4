package com.github.oop_assignment_4.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailSendDto {
	private Long userId;
	private String body;
	private String subject;
	private String[] to;
	private String priority;
	private List<AttachmentDTO> attachments;
}
