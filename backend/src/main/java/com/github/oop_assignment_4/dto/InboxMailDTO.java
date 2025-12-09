package com.github.oop_assignment_4.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class InboxMailDTO {
	private ReceivedMailDataDTO data;
	private Long id;
}
