package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.DeletedMailDTO;
import com.github.oop_assignment_4.dto.InboxMailDTO;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.service.TrashProxy;
import com.github.oop_assignment_4.service.TrashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trash")
@CrossOrigin(origins = "*")
public class TrashController {

    @Autowired
    private TrashService trashService;
    @Autowired
    private TrashProxy trashProxy;

    @PostMapping
    public ResponseEntity<List<DeletedMailDTO>> getTrash(@RequestBody InboxRequest inboxRequest) {
        return ResponseEntity.ok(trashProxy.getTrash(inboxRequest));
    }

    @PutMapping("/restore/bulk")
    public ResponseEntity<String> restoreBulk(@RequestBody List<Long> ids) {
        trashService.restoreMails(ids);
        return ResponseEntity.ok("Restored successfully");
    }

    @DeleteMapping("/delete/bulk")
    public ResponseEntity<String> deleteBulk(@RequestBody List<Long> ids) {
        trashService.deleteMailsForever(ids);
        return ResponseEntity.ok("Deleted forever");
    }
}