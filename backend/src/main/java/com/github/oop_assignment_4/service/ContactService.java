package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.ContactDTO;
import com.github.oop_assignment_4.model.Contact;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.repository.ContactRepository;
import com.github.oop_assignment_4.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public Contact createContact(ContactDTO request) {

        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // get all users that the emails refer to
        Set<User> contactUsers = new HashSet<>();
        for (String email : request.getContactEmails()) {
            userRepository.findByEmail(email)
                    .ifPresent(contactUsers::add);
        }

        Contact contact = Contact.builder()
                .name(request.getName())
                .user(owner)
                .contactUsers(contactUsers)
                .build();

        return contactRepository.save(contact);
    }

    public Contact editContact(ContactDTO request) {
        Contact existing = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        existing.setName(request.getName());

        Set<User> updatedUsers = new HashSet<>();
        for (String email : request.getContactEmails()) {
            userRepository.findByEmail(email)
                    .ifPresent(updatedUsers::add);
        }

        existing.setContactUsers(updatedUsers);

        return contactRepository.save(existing);
    }


    public void deleteContact(ContactDTO request) {
        Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contactRepository.delete(contact);
    }
}
