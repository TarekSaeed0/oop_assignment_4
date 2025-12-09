package com.github.oop_assignment_4.exception;

import org.springframework.http.HttpStatus;

public class AttachmentStorageException extends ApiException {
  public AttachmentStorageException(String message) {
    super("ATTACHMENT_STORAGE_ERROR", message,
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
