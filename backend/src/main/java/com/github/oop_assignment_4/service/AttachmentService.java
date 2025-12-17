package com.github.oop_assignment_4.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.github.oop_assignment_4.dto.AttachmentDTO;
import com.github.oop_assignment_4.exception.AttachmentNotFoundException;
import com.github.oop_assignment_4.exception.AttachmentStorageException;
import com.github.oop_assignment_4.exception.EmptyAttachmentException;
import com.github.oop_assignment_4.mapper.AttachmentMapper;
import com.github.oop_assignment_4.model.Attachment;
import com.github.oop_assignment_4.repository.AttachmentRepository;

@Service

public class AttachmentService {
	private final AttachmentRepository attachmentRepository;
	private final AttachmentMapper attachmentMapper;

	private final Path storageLocation = Paths.get("attachments");

	public AttachmentService(AttachmentRepository attachmentRepository,
			AttachmentMapper attachmentMapper) {
		this.attachmentRepository = attachmentRepository;
		this.attachmentMapper = attachmentMapper;

		try {
			Files.createDirectories(storageLocation);
		} catch (IOException e) {
			throw new AttachmentStorageException(
					"Failed to create attachments storage directory.");
		}
	}

	public AttachmentDTO uploadAttachment(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new EmptyAttachmentException();
			}

			String name = StringUtils.cleanPath(file.getOriginalFilename());

			String extension =
					name.contains(".") ? name.substring(name.lastIndexOf('.')) : "";

			Path path = this.storageLocation
					.resolve(UUID.randomUUID().toString() + extension);

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			}

			Attachment attachment = Attachment.builder().path(path.toString())
					.name(file.getOriginalFilename()).type(file.getContentType())
					.size(file.getSize()).build();

			return attachmentMapper.toDTO(attachmentRepository.save(attachment));
		} catch (IOException e) {
			throw new AttachmentStorageException("Failed to store attachment.");
		}
	}

	public Resource getAttachmentResource(Long id) {
		Attachment attachment = attachmentRepository.findById(id)
				.orElseThrow(() -> new AttachmentNotFoundException(id));

		try {
			Path path = Paths.get(attachment.getPath());
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new AttachmentNotFoundException(id);
			}
		} catch (MalformedURLException e) {
			throw new AttachmentStorageException("Failed to read attachment.");
		}
	}

	public List<AttachmentDTO> getAttachments() {
		return attachmentRepository.findAll().stream().map(attachmentMapper::toDTO)
				.toList();
	}

	public AttachmentDTO getAttachment(Long id) {
		return attachmentRepository.findById(id).map(attachmentMapper::toDTO)
				.orElseThrow(() -> new AttachmentNotFoundException(id));
	}
}
