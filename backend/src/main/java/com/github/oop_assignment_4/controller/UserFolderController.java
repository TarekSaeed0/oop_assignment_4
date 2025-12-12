package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.FolderRequest;
import com.github.oop_assignment_4.dto.MailToFolderRequest;
import com.github.oop_assignment_4.model.UserFolder;
import com.github.oop_assignment_4.service.UserFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/UserFolder")
public class UserFolderController {

    @Autowired
    UserFolderService userFolderService;

    @PostMapping("/createFolder")
    public void createFolder(@RequestBody FolderRequest folderRequest) {
        userFolderService.createFolder(folderRequest.name(), folderRequest.id());
    }

    @PostMapping("/addMailToFolder")
    public void addMailToFolder(@RequestBody MailToFolderRequest request) {
        userFolderService.addToFolder(request.userId(), request.folderName(), request.mailIds());
    }

    @PostMapping("/deleteMailFromFolder")
    public void deleteMailFromFolder(@RequestBody MailToFolderRequest request) {
        userFolderService.deleteFromFolder(request.userId(), request.folderName(), request.mailIds());
    }

    @GetMapping("/isValidName")
    public boolean isValidName(@RequestParam String name, @RequestParam Long id) {
        return userFolderService.isValidNameForUserFolder(name, id);
    }

    @GetMapping("getUserFolders")
    public List<UserFolder> getUserFolders(@RequestParam Long userId) {
        return userFolderService.getUserFolders(userId);
    }

}