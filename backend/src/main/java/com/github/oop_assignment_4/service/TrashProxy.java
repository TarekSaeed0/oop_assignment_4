package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.DeletedMailDTO;
import com.github.oop_assignment_4.dto.InboxRequest;
import com.github.oop_assignment_4.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Primary
@Service
public class TrashProxy  extends  TrashService{
@Autowired
MailRepository mailRepository;

    public List<DeletedMailDTO> getTrash(InboxRequest inboxRequest) {

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusMinutes(3);//delete before 7 mins
        mailRepository.deleteByDeletedAtBefore(thirtyDaysAgo);

        return super.getTrash(inboxRequest);
       }

    }
