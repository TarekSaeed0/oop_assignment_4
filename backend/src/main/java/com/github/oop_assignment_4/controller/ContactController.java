package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.ContactDTO;
import com.github.oop_assignment_4.model.Contact;
import com.github.oop_assignment_4.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // ---------------- Create Contact ----------------
    @PostMapping("/createContact")
    public ResponseEntity<Contact> createContact(@RequestBody ContactDTO request) {
        Contact created = contactService.createContact(request);
        return ResponseEntity.ok(created);
    }

    // ---------------- Edit Contact ----------------
    @PutMapping("/editContact/{id}")
    public ResponseEntity<Contact> editContact(@PathVariable Long id, @RequestBody ContactDTO request) {
        request.setContactId(id);
        Contact updated = contactService.editContact(request);
        return ResponseEntity.ok(updated);
    }

    // ---------------- Delete Contact ----------------
    @DeleteMapping("/deleteContact/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id, @RequestBody ContactDTO request) {
        request.setContactId(id);
        contactService.deleteContact(request);
        return ResponseEntity.ok("Contact deleted successfully");
    }
}
