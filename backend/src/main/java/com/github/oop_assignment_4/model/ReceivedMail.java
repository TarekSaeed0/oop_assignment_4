package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "sent_mail_id", nullable = false)
	private SentMail sentMail;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_folder_id")
	private UserFolder userFolder;

	private LocalDateTime deletedAt;

	private String subject;

	private String body;

	@Column(nullable = false)
	private Priority priority;

	String senderEmail;
}
