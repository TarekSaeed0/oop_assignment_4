package com.github.oop_assignment_4.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "mail_data_receivers",
			joinColumns = @JoinColumn(name = "mail_data_id"),
			inverseJoinColumns = @JoinColumn(name = "receiver_id"))
	private Set<User> receivers;

	private String subject;

	private String body;

	@Column(nullable = false)
	@Builder.Default
	private com.github.oop_assignment_4.model.Priority priority = Priority.NORMAL;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sent_mail_attachments",
			joinColumns = @JoinColumn(name = "sent_mail_id"),
			inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	private Set<Attachment> attachments;

	@Column(nullable = false)
	private LocalDateTime sentAt;

	@JsonIgnore
	@OneToMany(mappedBy = "data", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<Mail> mails;
}