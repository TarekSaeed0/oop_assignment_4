package com.github.oop_assignment_4.mapper;

import org.mapstruct.Mapper;
import com.github.oop_assignment_4.dto.AttachmentDTO;
import com.github.oop_assignment_4.model.Attachment;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
  AttachmentDTO toDTO(Attachment attachment);
}
