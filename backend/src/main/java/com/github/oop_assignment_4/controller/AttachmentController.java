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
	public ResponseEntity<AttachmentDTO> uploadAttachment(
			@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(attachmentService.uploadAttachment(file));
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
		Resource resource = attachmentService.downloadAttachment(id);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"" + resource.getFilename() + "\"")
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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
		attachmentService.deleteAttachment(id);
		return ResponseEntity.noContent().build();
	}
}
