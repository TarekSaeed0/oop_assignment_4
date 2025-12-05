package com.github.oop_assignment_4.exception;

import org.springframework.http.HttpStatus;

public class AttachmentNotFoundException extends ApiException {
  public AttachmentNotFoundException() {
    super("ATTACHMENT_NOT_FOUND", "Attachment not found", HttpStatus.NOT_FOUND);
  }

  public AttachmentNotFoundException(Long id) {
    super("ATTACHMENT_NOT_FOUND", "Attachment with id " + id + " not found",
        HttpStatus.NOT_FOUND);
  }
}
