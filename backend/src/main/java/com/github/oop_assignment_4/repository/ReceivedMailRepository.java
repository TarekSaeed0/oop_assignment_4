package com.github.oop_assignment_4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.oop_assignment_4.model.ReceivedMail;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ReceivedMailRepository extends JpaRepository<ReceivedMail, Long> {

	@Query(
			nativeQuery = true
			,value = "SELECT * FROM maildb.received_mails where receiver_id = ?1"
	)
	public Set<ReceivedMail> getReceivedMailByReceiverId(Long id);
}
