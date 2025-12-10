package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Contact;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserId(Long userId);
}
