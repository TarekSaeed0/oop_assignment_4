package com.github.oop_assignment_4.model;

import java.util.Set;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drafts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Draft {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "draft_receivers",
			joinColumns = @JoinColumn(name = "draft_id"),
			inverseJoinColumns = @JoinColumn(name = "receiver_id"))
	private Set<User> receivers;

	private String subject;

	private String body;

	@Column(nullable = false)
	private Priority priority;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "draft_attachments",
			joinColumns = @JoinColumn(name = "draft_id"),
			inverseJoinColumns = @JoinColumn(name = "attachment_id"))
	private Set<Attachment> attachments;
}
