package com.github.oop_assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class InboxDTO {
	List<MailDto> receivedMail;
}
