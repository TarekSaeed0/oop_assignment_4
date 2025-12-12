package com.github.oop_assignment_4.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.github.oop_assignment_4.dto.AttachmentDTO;
import com.github.oop_assignment_4.service.AttachmentService;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
	private final AttachmentService attachmentService;

	public AttachmentController(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@PostMapping("/upload")
	public ResponseEntity<List<AttachmentDTO>> uploadAttachments(
			@RequestParam("files") List<MultipartFile> files) {
		return ResponseEntity.ok(files.stream()
				.map(file -> attachmentService.uploadAttachment(file)).toList());
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
		AttachmentDTO attachmentDTO = attachmentService.getAttachment(id);
		Resource resource = attachmentService.getAttachmentResource(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + attachmentDTO.getName() + "\"")
				.body(resource);
	}

	@GetMapping("/{id}/view")
	public ResponseEntity<Resource> viewAttachment(@PathVariable Long id) {
		AttachmentDTO attachmentDTO = attachmentService.getAttachment(id);
		Resource resource = attachmentService.getAttachmentResource(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"" + attachmentDTO.getName() + "\"")
				.body(resource);
	}

	@GetMapping
	public ResponseEntity<List<AttachmentDTO>> getAttachments() {
		return ResponseEntity.ok(attachmentService.getAttachments());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AttachmentDTO> getAttachment(@PathVariable Long id) {
		return ResponseEntity.ok(attachmentService.getAttachment(id));
	}
}
