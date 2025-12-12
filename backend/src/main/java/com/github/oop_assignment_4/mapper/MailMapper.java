package com.github.oop_assignment_4.mapper;

import com.github.oop_assignment_4.dto.InboxMailDTO;
import com.github.oop_assignment_4.model.Mail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MailMapper {
	InboxMailDTO toDTO(Mail mail);
}
