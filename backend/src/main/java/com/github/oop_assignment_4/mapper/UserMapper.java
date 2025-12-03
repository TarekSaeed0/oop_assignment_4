package com.github.oop_assignment_4.mapper;

import org.mapstruct.Mapper;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserDTO toDTO(User user);
}
