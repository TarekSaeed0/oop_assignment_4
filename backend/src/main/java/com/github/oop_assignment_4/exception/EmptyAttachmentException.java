package com.github.oop_assignment_4.exception;

import org.springframework.http.HttpStatus;

public class EmptyAttachmentException extends ApiException {
  public EmptyAttachmentException() {
    super("EMPTY_ATTACHMENT", "Attachment is empty.", HttpStatus.BAD_REQUEST);
  }

}
