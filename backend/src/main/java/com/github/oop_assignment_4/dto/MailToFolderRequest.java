package com.github.oop_assignment_4.dto;

import java.util.List;

public record MailToFolderRequest(Long userId, String folderName, List<Long> mailIds) {
}