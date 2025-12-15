package com.github.oop_assignment_4.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftDTO {
    private Long id; // of Sender
    private String senderEmail;
    private String senderName;
    private Set<UserReferenceDTO> receivers; // A lightweight DTO for users
    private String subject;
    private String body;
    private String priority; // Representing Priority enum as a String
}