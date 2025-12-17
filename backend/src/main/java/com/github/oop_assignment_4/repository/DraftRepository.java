package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Draft;

import java.util.List;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findBySenderId(Long senderId);
}
