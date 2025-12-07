package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.MailToFolderRequest;
import com.github.oop_assignment_4.model.Mail;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.model.UserFolder;
import com.github.oop_assignment_4.repository.MailRepository;
import com.github.oop_assignment_4.repository.UserFolderRepository;
import com.github.oop_assignment_4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserFolderService {
    @Autowired
    UserFolderRepository userFolderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MailRepository mailRepository;

    public UserFolder createFolder(String folderName, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // Check for duplicates
        if (userFolderRepository.findByNameAndUser(folderName, user).isPresent()) {
            throw new RuntimeException("Folder with the same name for this user already exists");
        }

        UserFolder userFolder = UserFolder.builder()
                .name(folderName)
                .user(user)
                .build();

        return userFolderRepository.save(userFolder);
    }

    @Transactional
    public void addToFolder(Long userId, String folderName, List<Long> mailIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserFolder userFolder = userFolderRepository
                .findByNameAndUser(folderName, user)
                .orElseThrow(() -> new RuntimeException("There is no folder with such name"));

        for (Long mailId : mailIds) {
            Mail mail = mailRepository.findById(mailId)
                    .orElseThrow(() -> new RuntimeException("Mail not found with ID: " + mailId));

            mail.setUserFolder(userFolder);
            userFolder.getMails().add(mail);
            mailRepository.save(mail);
        }
    }

    @Transactional
    public void deleteFromFolder(Long userId, String folderName, List<Long> mailIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserFolder userFolder = userFolderRepository
                .findByNameAndUser(folderName, user)
                .orElseThrow(() -> new RuntimeException("There is no folder with such name"));

        for (Long mailId : mailIds) {
            Mail mail = mailRepository.findById(mailId)
                    .orElseThrow(() -> new RuntimeException("Mail not found with ID: " + mailId));

            if (mail.getUserFolder() == null || !mail.getUserFolder().getId().equals(userFolder.getId())) {
                throw new RuntimeException("Mail ID " + mailId + " is not currently in the '" + folderName + "' folder.");
            }

            mail.setUserFolder(null);
            userFolder.getMails().remove(mail);            mailRepository.save(mail);
        }
    }
}