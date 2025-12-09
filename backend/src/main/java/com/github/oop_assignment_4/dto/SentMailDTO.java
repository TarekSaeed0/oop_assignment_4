package com.github.oop_assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SentMailDTO {
	private SentMailDataDTO data;
	private Long id;
}
