package com.github.oop_assignment_4.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
