package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.Draft;

public interface DraftRepository extends JpaRepository<Draft, Long> {
}
