package com.github.oop_assignment_4.dto;


import com.github.oop_assignment_4.model.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
	private Long userId;
	private String body;
	private String subject;
	private String[] to;
	private Priority priority;
}
