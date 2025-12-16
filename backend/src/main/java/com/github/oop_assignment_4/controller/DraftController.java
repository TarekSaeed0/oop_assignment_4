package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.DraftDTO;
import com.github.oop_assignment_4.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {

    private final DraftService draftService;

    @Autowired
    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    /**
     * POST /api/drafts
     * Creates a new draft.
     * @param draftDto The draft data submitted by the user.
     * @return The created DraftDto with the generated ID, and HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<DraftDTO> createDraft(@RequestBody DraftDTO draftDto) {
        try {
            // Service handles DTO to Entity conversion and saving
            DraftDTO createdDraft = draftService.createDraft(draftDto);
            return new ResponseEntity<>(createdDraft, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle specific errors like user not found (404) or validation issues (400)
            System.err.println("Error creating draft: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/drafts/user/{userId}
     * Fetches all drafts associated with a specific user (sender).
     * @param userId The ID of the sender whose drafts are to be retrieved.
     * @return A List of DraftDto objects and HTTP 200 OK. Returns an empty list [] if the user has no drafts.
     */
    @GetMapping("user/{userId}")
    public ResponseEntity<List<DraftDTO>> getDrafts(@PathVariable Long userId) {
        List<DraftDTO> drafts = draftService.getDrafts(userId);

        return new ResponseEntity<>(drafts, HttpStatus.OK);
    }

    @GetMapping("draft/{id}")
    public ResponseEntity<DraftDTO> getDraft(@PathVariable Long id) {
        DraftDTO draft = draftService.getDraft(id);

        return new ResponseEntity<>(draft, HttpStatus.OK);
    }

    /**
     * PUT /api/drafts/{id}
     * Updates an existing draft.
     * @param id The ID of the draft to update.
     * @param draftDto The updated draft data submitted by the user.
     * @return The updated DraftDto and HTTP 200 OK, or HTTP 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DraftDTO> updateDraft(@PathVariable Long id, @RequestBody DraftDTO draftDto) {

        // Service handles updating the existing entity and returns the updated DTO
        DraftDTO updatedDraft = draftService.updateDraft(id, draftDto);
        return new ResponseEntity<>(updatedDraft, HttpStatus.OK);

    }

    /**
     * DELETE /api/drafts/{id}
     * Deletes a specific draft by ID.
     * @param id The ID of the draft to delete.
     * @return HTTP 204 No Content upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDraft(@PathVariable Long id) {
            String msg = draftService.deleteDraft(id);
            return new ResponseEntity<>(msg,HttpStatus.OK);
    }


}