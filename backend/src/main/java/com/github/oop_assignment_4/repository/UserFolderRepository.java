package com.github.oop_assignment_4.repository;

import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.model.UserFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFolderRepository extends JpaRepository<UserFolder, Long> {

    List<UserFolder> findByName(String name);

    Optional<UserFolder> findByNameAndUser(String name, User user);
     List<UserFolder> findByUser(User user);

}