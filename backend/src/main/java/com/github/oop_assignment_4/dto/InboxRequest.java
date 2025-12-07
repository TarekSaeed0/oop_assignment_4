package com.github.oop_assignment_4.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InboxRequest {
	@NotNull
	private Long userId;
	@NotNull
	private int page;
	@NotNull
	private int size;
	@NotNull
	private String searchBy;
	@NotNull
	private String filterBy;
	@NotNull
	private String priority;
	@NotNull
	private boolean hasAttachment;

}
