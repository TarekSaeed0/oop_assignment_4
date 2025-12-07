package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.ContactDTO;
import com.github.oop_assignment_4.model.Contact;
import com.github.oop_assignment_4.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // ---------------- Create Contact ----------------
    @PostMapping("/createContact")
    public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO request) {
        ContactDTO created = contactService.createContact(request);
        return ResponseEntity.ok(created);
    }

    // ---------------- Edit Contact ----------------
    @PutMapping("/editContact/{id}")
    public ResponseEntity<ContactDTO> editContact(@PathVariable Long id, @RequestBody ContactDTO request) {
        request.setContactId(id);
        ContactDTO updated = contactService.editContact(request);
        return ResponseEntity.ok(updated);
    }

    // ---------------- Delete Contact ----------------
    @DeleteMapping("/deleteContact/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok("Contact deleted successfully");
    }

    // to fetch a data from specific contact by his ID
    @GetMapping("/getContact/{id}")
    public ResponseEntity<ContactDTO> getContact(@PathVariable Long id) {
        ContactDTO contactDTO = contactService.getContactDTO(id);
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/user/{userId}/contacts")
    public ResponseEntity<List<ContactDTO>> getAllContactsOfUser(@PathVariable Long userId) {
        List<ContactDTO> contacts = contactService.getAllContactsOfUser(userId);
        return ResponseEntity.ok(contacts);
    }
}
