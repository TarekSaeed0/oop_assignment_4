package com.github.oop_assignment_4.controller;

import com.github.oop_assignment_4.dto.FolderRequest;
import com.github.oop_assignment_4.dto.MailToFolderRequest;
import com.github.oop_assignment_4.service.UserFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}