package ru.tersoft.ticketsale.service.impl;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.entity.Dialog;
import ru.tersoft.ticketsale.entity.Message;
import ru.tersoft.ticketsale.repository.AccountRepository;
import ru.tersoft.ticketsale.repository.DialogRepository;
import ru.tersoft.ticketsale.repository.MessageRepository;
import ru.tersoft.ticketsale.service.DialogService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("DialogService")
@Transactional(rollbackFor=LockAcquisitionException.class)
public class DialogServiceImpl implements DialogService {
    private final DialogRepository dialogRepository;
    private final AccountRepository accountRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public DialogServiceImpl(DialogRepository dialogRepository, AccountRepository accountRepository, MessageRepository messageRepository) {
        this.dialogRepository = dialogRepository;
        this.accountRepository = accountRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public ResponseEntity<?> getById(UUID id) {
        Dialog dialog = dialogRepository.findOne(id);
        if(dialog != null)
            return ResponseFactory.createResponse(dialog);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");

    }

    public ResponseEntity<?> getByAnswered(int page, int limit) {
        return ResponseFactory.createResponse(dialogRepository.findByAnswered(new PageRequest(page, limit)));
    }

    public ResponseEntity<?> getOpenDialogs(int page, int limit) {
        return ResponseFactory.createResponse(dialogRepository.findOpenDialogs(new PageRequest(page, limit)));
    }

    @Override
    public ResponseEntity<?> getByUser(UUID userid, int page, int limit) {
        Account account = accountRepository.findOne(userid);
        if(account != null)
            return ResponseFactory.createResponse(dialogRepository.findByUser(account, new PageRequest(page, limit)));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
    }

    @Override
    public ResponseEntity<?> start(Message message, String title) {
        message.setType("question");
        message.setUser(accountRepository.findOne(message.getUser().getId()));
        Message addedMessage = messageRepository.saveAndFlush(message);
        Dialog dialog = new Dialog();
        List<Message> messages = new ArrayList<>();
        messages.add(addedMessage);
        dialog.setTitle(title);
        dialog.setClosed(false);
        dialog.setAnswered(false);
        dialog.setMessages(messages);
        dialog = dialogRepository.saveAndFlush(dialog);
        addedMessage.setDialog(dialog);
        return ResponseFactory.createResponse(messageRepository.saveAndFlush(addedMessage));
    }

    @Override
    public ResponseEntity<?> addAnswer(UUID dialogid, Message message, Boolean closed) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        if(dialog != null) {message.setDialog(dialog);
            message.setType("answer");
            message.setUser(accountRepository.findOne(message.getUser().getId()));
            Message addedMessage = messageRepository.saveAndFlush(message);
            List<Message> messages = dialog.getMessages();
            messages.add(addedMessage);
            dialog.setMessages(messages);
            dialog.setAnswered(true);
            if(closed != null) {
                if(closed) dialog.setClosed(true);
            }
            return ResponseFactory.createResponse(dialogRepository.saveAndFlush(dialog));

        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }

    }

    @Override
    public ResponseEntity<?> addQuestion(UUID dialogid, Message message) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        if(dialog != null) {
            if(dialog.getClosed())
                return ResponseFactory.createErrorResponse(HttpStatus.LOCKED, "Such dialog was closed");
            message.setDialog(dialog);
            message.setUser(accountRepository.findOne(message.getUser().getId()));
            message.setType("question");
            Message addedMessage = messageRepository.saveAndFlush(message);
            List<Message> messages = dialog.getMessages();
            messages.add(addedMessage);
            dialog.setMessages(messages);
            dialog.setAnswered(false);
            return ResponseFactory.createResponse(dialogRepository.saveAndFlush(dialog));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> setClosed(UUID dialogid, Boolean closed) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        if(dialog != null) {
            dialog.setClosed(closed);
            return ResponseFactory.createResponse(dialog);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        if(dialogRepository.findOne(id) != null) {
            dialogRepository.delete(id);
            return ResponseFactory.createResponse();
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }
}
