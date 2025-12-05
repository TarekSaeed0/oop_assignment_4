package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstrucimport lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mail_datas")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  MailData {
	@Id	@GenertedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "mail_data_receivers",
			joinColumns = @JoinColumn(name = "mail_data_id"),
			inverseJo Columns = @JoinColumn(name = "receiver_id"))
	private Set<User> receivers;

	private String subject;

	private String body;


		privat Priority priority; 
	@ManyToMany(fetch = FetchType.EAGER)	@JoinTable(name = "sent_mail_attachments",
			joinColumns = @JoinColumn(name = "sent_mail_id"),
			inverseJoinColumns = @JoinColumn(name  ivate Set<Attachment> attachments; 
	@Column(nullable = false)	privat LocalDateTime sentAt;

	@OneToMany(mappedBy = "data", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<Mail> mails;
}
   