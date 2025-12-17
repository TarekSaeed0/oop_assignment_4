package com.github.oop_assignment_4.mapper;

import com.github.oop_assignment_4.dto.*;
import com.github.oop_assignment_4.dto.DeletedMailDTO;
import com.github.oop_assignment_4.model.MailSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DeletedMailMapper {

    public DeletedMailDTO fromSentMail(SentMailDTO sentMail) {
        SentMailDataDTO data = sentMail.getData();

        return DeletedMailDTO.builder()
                .id(sentMail.getId())
                .subject(data.getSubject())
                .body(data.getBody())
                .priority(data.getPriority())
                .date(data.getSentAt())
                .attachments(data.getAttachments())
                .sender(data.getSender())
                .receivers(data.getReceivers())
                .source(MailSource.SENT)
                .build();
    }

    public DeletedMailDTO fromInboxMail(InboxMailDTO inboxMail) {
        ReceivedMailDataDTO data = inboxMail.getData();

        return DeletedMailDTO.builder()
                .id(inboxMail.getId())
                .subject(data.getSubject())
                .body(data.getBody())
                .priority(data.getPriority())
                .date(data.getSentAt())
                .attachments(data.getAttachments())
                .sender(data.getSender())
                .receivers(data.getReceiver() != null ? List.of(data.getReceiver()) : Collections.emptyList())
                .source(MailSource.INBOX)       // Flag as INBOX
                .build();
    }
}