package com.github.oop_assignment_4.dto;

import java.util.Set;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {
    private String name;
    private Long userId;
    private Set<String> contactEmails;
    private Long contactId;
}
