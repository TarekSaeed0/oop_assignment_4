package com.github.oop_assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
public class SenderDTO {
	private String email;
	private String name;

}
