package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.SentMail;

public interface SentMailRepository extends JpaRepository<SentMail, Long> {
}
