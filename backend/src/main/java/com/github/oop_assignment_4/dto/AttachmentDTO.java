package com.github.oop_assignment_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
  private Long id;
  private String name;
	private String type;
	private Long size;
}
