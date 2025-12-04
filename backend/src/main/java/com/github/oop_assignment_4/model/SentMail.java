package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sent_mails")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentMail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@OneToMany(mappedBy = "sentMail", cascade = CascadeType.ALL)
	private Set<ReceivedMail> receivedMails;

	private String subject;

	private String body;

	@Column(nullable = false)
	private Priority priority;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sent_mail_attachments",
			joinColumns = @JoinColumn(name = "sent_mail_id"),
			inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	private Set<Attachment> attachments;

	@Column(nullable = false)
	private LocalDateTime sentAt;
}
