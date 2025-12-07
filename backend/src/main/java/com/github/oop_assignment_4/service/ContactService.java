package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.ContactDTO;
import com.github.oop_assignment_4.model.Contact;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.repository.ContactRepository;
import com.github.oop_assignment_4.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactDTO createContact(ContactDTO request) {

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

        Contact savedContact = contactRepository.save(contact);

        return ContactDTO.builder()
                .userId(savedContact.getId())
                .name(savedContact.getName())
                .userId(owner.getId())
                .contactEmails(contactUsers.stream().map(User::getEmail).collect(Collectors.toSet()))
                .build();
    }

    public ContactDTO editContact(ContactDTO request) {
        Contact existing = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        existing.setName(request.getName());

        Set<User> updatedUsers = new HashSet<>();
        for (String email : request.getContactEmails()) {
            userRepository.findByEmail(email)
                    .ifPresent(updatedUsers::add);
        }

        existing.setContactUsers(updatedUsers);

        Contact editedContact = contactRepository.save(existing);

        return ContactDTO.builder()
                .userId(editedContact.getId())
                .name(editedContact.getName())
                .userId(existing.getId())
                .contactEmails(updatedUsers.stream().map(User::getEmail).collect(Collectors.toSet()))
                .build();
    }


    public void deleteContact(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contactRepository.delete(contact);
    }

    public ContactDTO getContactDTO(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        return ContactDTO.builder()
                .contactId(contact.getId())
                .name(contact.getName())
                .userId(contact.getUser().getId())
                .contactEmails(contact.getContactUsers()
                        .stream()
                        .map(User::getEmail)
                        .collect(Collectors.toSet()))
                .build();
    }

    public List<ContactDTO> getAllContactsOfUser(Long userId) {
        // Fetch all contacts where the owner is this user
        List<Contact> contacts = contactRepository.findByUserId(userId);

        // Convert each contact to ContactDTO
        return contacts.stream()
                .map(contact -> ContactDTO.builder()
                        .contactId(contact.getId())
                        .name(contact.getName())
                        .userId(contact.getUser().getId())
                        .contactEmails(contact.getContactUsers()
                                .stream()
                                .map(User::getEmail)
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

}
