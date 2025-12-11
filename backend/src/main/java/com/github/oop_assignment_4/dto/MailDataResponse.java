package com.github.oop_assignment_4.dto;

import com.github.oop_assignment_4.model.Priority;
import java.time.LocalDateTime;

public record MailDataResponse(
        String senderName,
        String senderEmail,
        String subject,
        String body,
        LocalDateTime sentAt,
        Priority priority
) {}