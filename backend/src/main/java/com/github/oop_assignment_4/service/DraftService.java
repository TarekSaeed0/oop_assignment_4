package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.model.*;
import com.github.oop_assignment_4.repository.*;
import com.github.oop_assignment_4.dto.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.oop_assignment_4.exception.AttachmentNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DraftService {

    private final DraftRepository draftRepository;
    private final UserRepository userRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    // Assuming Priority enum is accessible here for conversion

    public DraftService(DraftRepository draftRepository, UserRepository userRepository) {
        this.draftRepository = draftRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param draftDto The DTO from the front-end.
     * @return The saved Draft as a DTO.
     */
    @Transactional
    public DraftDTO createDraft(DraftDTO draftDto) {
        User sender = userRepository.findByEmail(draftDto.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + draftDto.getSenderEmail()));

        Set<User> receivers = draftDto.getReceivers().stream()
                .map(refDto -> userRepository.findByEmail(refDto.getEmail())
                        .orElseThrow(() -> new RuntimeException("Receiver ID not found: " + refDto.getId())))
                .collect(Collectors.toSet());

        // Fetch Attachments
        Set<Attachment> attachments = new HashSet<>();
        if (draftDto.getAttachments() != null) {
            attachments = draftDto.getAttachments().stream()
                    .map(attachmentDTO -> attachmentRepository
                            .findById(attachmentDTO.getId()).orElseThrow(
                                    () -> new AttachmentNotFoundException(attachmentDTO.getId())))
                    .collect(Collectors.toSet());
        }

        // Build the Entity object
        Draft draft = Draft.builder()
                .sender(sender)
                .receivers(receivers)
                .subject(draftDto.getSubject())
                .body(draftDto.getBody())
                .priority(Enum.valueOf(com.github.oop_assignment_4.model.Priority.class, draftDto.getPriority()))
                .build();

        Draft savedDraft = draftRepository.save(draft);

        return DraftMapper.toDto(savedDraft);
    }

    /**
     * Fetches all Drafts for a given User ID.
     * @param userId The ID of the user (sender).
     * @return A List of DraftDto objects.
     */
    public List<DraftDTO> getDrafts(Long userId) {
        // 1. Fetch the list of Draft entities using the custom repository method
        List<Draft> drafts = draftRepository.findBySenderId(userId);

        // 2. Map the List of Entities to a List of DTOs
        return drafts.stream()
                .map(DraftMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * @param id The ID of the draft to update.
     * @param updateDto A DTO containing the new details (subject, body, etc.).
     * @return The updated Draft as a DTO.
     */
    @Transactional
    public DraftDTO updateDraft(Long id, DraftDTO updateDto) {
        Draft existingDraft = draftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Draft not found with ID: " + id));

        existingDraft.setSubject(updateDto.getSubject());
        existingDraft.setBody(updateDto.getBody());
        existingDraft.setPriority(Enum.valueOf(com.github.oop_assignment_4.model.Priority.class, updateDto.getPriority()));

        Set<User> updatedReceivers = new HashSet<>();
        if (updateDto.getReceivers() != null) {
            updatedReceivers = updateDto.getReceivers().stream()
                    // Assume getReceivers() returns a list of DTOs with User ID/Ref
                    .map(refDto -> userRepository.findByEmail(refDto.getEmail())
                            .orElseThrow(() -> new RuntimeException("Receiver ID not found: " + refDto.getId())))
                    .collect(Collectors.toSet());
        }
        existingDraft.setReceivers(updatedReceivers);

        Set<Attachment> updatedAttachments = new HashSet<>();
        if (updateDto.getAttachments() != null) {
            updatedAttachments = updateDto.getAttachments().stream()
                    .map(attachmentDTO -> attachmentRepository
                            .findById(attachmentDTO.getId()).orElseThrow(
                                    () -> new AttachmentNotFoundException(attachmentDTO.getId())))
                    .collect(Collectors.toSet());
        }
        existingDraft.setAttachments(updatedAttachments);

        Draft updatedDraft = draftRepository.save(existingDraft);

        return DraftMapper.toDto(updatedDraft);
    }

    /** @param id The ID of the draft to delete.*/
    @Transactional
    public String deleteDraft(Long id) {
        draftRepository.deleteById(id);
        return "Successfully deleted ✅";
    }

    @Transactional
    public DraftDTO getDraft(Long id) {
        Draft draft = draftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("draft not found"));
        return DraftMapper.toDto(draft);
    }
}
class DraftMapper {

    // --- Entity to DTO Conversion ---
    public static DraftDTO toDto(Draft draft) {
        if (draft == null) {
            return null;
        }

        Set<UserReferenceDTO> receiverDtos = draft.getReceivers().stream()
                .map(DraftMapper::toUserReferenceDto)
                .collect(Collectors.toSet());

        return DraftDTO.builder()
                .id(draft.getId())
                .senderEmail(draft.getSender().getEmail())
                .senderName(draft.getSender().getName())
                .receivers(receiverDtos)
                .subject(draft.getSubject())
                .body(draft.getBody())
                // Assuming Priority.name() gives the string representation
                .priority(draft.getPriority().name())
                .build();
    }

    public static UserReferenceDTO toUserReferenceDto(User user) {
        return UserReferenceDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }


}