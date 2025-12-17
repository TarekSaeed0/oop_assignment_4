package com.github.oop_assignment_4.service;

import com.github.oop_assignment_4.dto.*;
import com.github.oop_assignment_4.dto.DeletedMailDTO;
import com.github.oop_assignment_4.model.*;
import com.github.oop_assignment_4.model.mailCriterion.*;
import com.github.oop_assignment_4.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrashService {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private UserRepository userRepository;


    private DeletedMailDTO toDeletedMailDto(Mail mail) {
        MailData data = mail.getData();
        User currentUser = mail.getUser(); // The owner of this trash item
        User sender = data.getSender();

        boolean isSender = currentUser.getEmail().equals(sender.getEmail());
        MailSource source = isSender ? MailSource.SENT : MailSource.INBOX;

        // 2. Map Sender
        SenderDTO senderDto = SenderDTO.builder()
                .email(sender.getEmail())
                .name(sender.getName())
                .build();

        // 3. Map Receivers
        List<SenderDTO> receiversList;
        if (source == MailSource.SENT) {
            receiversList = data.getReceivers().stream()
                    .map(r -> SenderDTO.builder().name(r.getName()).email(r.getEmail()).build())
                    .collect(Collectors.toList());
        } else {
            SenderDTO me = SenderDTO.builder()
                    .name(currentUser.getName())
                    .email(currentUser.getEmail())
                    .build();
            receiversList = List.of(me);
        }

        return DeletedMailDTO.builder()
                .id(mail.getId())
                .subject(data.getSubject())
                .body(data.getBody())
                .priority(data.getPriority())
                .date(data.getSentAt())
                .attachments(data.getAttachments())
                .sender(senderDto)
                .receivers(receiversList)
                .source(source)
                .build();
    }

    public List<DeletedMailDTO> toDeletedDTOs(List<Mail> mails) {
        List<DeletedMailDTO> dtos = new ArrayList<>();
        for (Mail mail : mails) {
            dtos.add(toDeletedMailDto(mail));
        }
        return dtos;
    }

    public InboxMailDTO toInboxMailDto(Mail mail) {
        User receiver = mail.getUser();
        MailData mailData = mail.getData();
        User sender = mail.getData().getSender();

        SenderDTO receiverDto = SenderDTO.builder()
                .email(receiver.getEmail())
                .name(receiver.getName())
                .build();

        SenderDTO senderDTO = SenderDTO.builder()
                .email(sender.getEmail())
                .name(sender.getName())
                .build();

        ReceivedMailDataDTO receivedMailDataDTO = ReceivedMailDataDTO.builder()
                .receiver(receiverDto)
                .sender(senderDTO)
                .sentAt(mailData.getSentAt())
                .subject(mailData.getSubject())
                .body(mailData.getBody())
                .attachments(mailData.getAttachments())
                .priority(mailData.getPriority())
                .build();

        return InboxMailDTO.builder()
                .data(receivedMailDataDTO)
                .id(mail.getId())
                .build();
    }

    public List<InboxMailDTO> toInboxDTO(List<Mail> receivedMail) {
        List<InboxMailDTO> inboxMailDTOS = new ArrayList<>();
        for (Mail mail : receivedMail) {
            inboxMailDTOS.add(toInboxMailDto(mail));
        }
        return inboxMailDTOS;
    }


    List<Mail> filter(boolean inbox, boolean isTrash, List<Mail> mailList, String filterBy,
                      String searchBy, String priority, boolean hasAttachment) {

        MailCriterion criterion;

        if (isTrash) {
            MailCriterion inboxSearch = new GeneralSearchCriterionForInbox(searchBy);
            MailCriterion sentSearch = new GeneralSearchCriterionForSent(searchBy);
            criterion = new OrCriterion(inboxSearch, sentSearch);
        } else {
            criterion = (inbox
                    ? new GeneralSearchCriterionForInbox(searchBy)
                    : new GeneralSearchCriterionForSent(searchBy));
        }

        if ("body".equals(filterBy)) {
            criterion = new AndCriterion(criterion, new FilterByBodyCriterion(searchBy));
        }
        if ("subject".equals(filterBy)) {
            criterion = new AndCriterion(criterion, new FilterBySubjectCriterion(searchBy));
        }
        if ("name".equals(filterBy)) {
            MailCriterion senderName = new FilterBySenderNameCriterion(searchBy);
            MailCriterion receiverName = new FilterByReceiverNameCriterion(searchBy);
            criterion = new AndCriterion(criterion, new OrCriterion(senderName, receiverName));
        }
        if ("email".equals(filterBy)) {
            MailCriterion senderEmail = new FilterBySenderEmailCriterion(searchBy);
            MailCriterion receiverEmail = new FilterByReceiverEmailCriterion(searchBy);
            criterion = new AndCriterion(criterion, new OrCriterion(senderEmail, receiverEmail));
        }
        if (hasAttachment) {
            criterion = new AndCriterion(criterion, new HasAttachmentCriterion());
        }
        if (!"any".equals(priority) && priority != null) {
            criterion = new AndCriterion(criterion, new PriorityCriterion(priority));
        }

        return criterion.meetsCriterion(mailList);
    }

    // --- Main Service Method (Updated Return Type) ---

    @Transactional
    public List<DeletedMailDTO> getTrash(InboxRequest inboxRequest) {

        List<Mail> allMail = mailRepository.findByUserId(inboxRequest.getUserId());

        MailCriterion deletedCriterion = new DeletedCriterion();
        List<Mail> deletedMails = deletedCriterion.meetsCriterion(allMail);

        List<Mail> filtered = filter(true, true, deletedMails,
                inboxRequest.getFilterBy(),
                inboxRequest.getSearchBy(),
                inboxRequest.getPriority(),
                inboxRequest.isHasAttachment());


        int start = (inboxRequest.getPage() - 1) * inboxRequest.getSize();
        if (start >= filtered.size() || start < 0) {
            return new ArrayList<>();
        }
        int end = Math.min(filtered.size(), start + inboxRequest.getSize());
        List<Mail> paged = filtered.subList(start, end);

        return toDeletedDTOs(paged);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredTrash() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        mailRepository.deleteByDeletedAtBefore(thirtyDaysAgo);
        System.out.println("Trash cleanup completed. Removed mails deleted before " + thirtyDaysAgo);
    }


    @Transactional
    public void restoreMails(List<Long> mailIds) {
        List<Mail> mails = mailRepository.findAllById(mailIds);

        for (Mail mail : mails) {
            mail.setDeletedAt(null);
        }

        mailRepository.saveAll(mails);
    }

    @Transactional
    public void deleteMailsForever(List<Long> mailIds) {
        mailRepository.deleteAllById(mailIds);
    }

}