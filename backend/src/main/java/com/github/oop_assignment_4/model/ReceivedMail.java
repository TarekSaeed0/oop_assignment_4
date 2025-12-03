package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "received_mails")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "sent_mail_id", nullable = false)
	private SentMail sentMail;

	@ManyToOne
	@JoinColumn(name = "user_folder_id")
	private UserFolder userFolder;

	private LocalDateTime deletedAt;
}
