package com.github.oop_assignment_4.dto;

public record MailResponse(
        Long id,
        MailDataResponse data,
        String folderName
) {}