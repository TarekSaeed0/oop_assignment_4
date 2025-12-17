package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mails")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "mail_data_id", nullable = false)
	private MailData data;

	@ManyToOne
	@JoinColumn(name = "user_folder_id")
	private UserFolder userFolder;

	private LocalDateTime deletedAt;
}
