package com.github.oop_assignment_4.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"drafts", "mails"})
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<Draft> drafts;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<Mail> mails;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserFolder> userFolders;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Contact> contacts;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new HashSet<>();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
