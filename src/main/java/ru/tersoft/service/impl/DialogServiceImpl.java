package ru.tersoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Message;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.repository.DialogRepository;
import ru.tersoft.repository.MessageRepository;
import ru.tersoft.service.DialogService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("DialogService")
@Transactional
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
    public Page<Dialog> getAll(int page, int limit) {
        return dialogRepository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Dialog getById(UUID id) {
        return dialogRepository.findOne(id);
    }

    @Override
    public Page<Dialog> getByAnswered(int page, int limit) {
        return dialogRepository.findByAnswered(new PageRequest(page, limit));
    }

    @Override
    public Page<Dialog> getByUser(UUID userid, int page, int limit) {
        Account account = accountRepository.findOne(userid);
        return dialogRepository.findByUser(account, new PageRequest(page, limit));
    }

    @Override
    public Dialog start(Message message, String title) {
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
        messageRepository.saveAndFlush(addedMessage);
        return dialog;
    }

    @Override
    public Dialog addAnswer(UUID dialogid, Message message, Boolean closed) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        message.setDialog(dialog);
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
        return dialogRepository.saveAndFlush(dialog);
    }

    @Override
    public Dialog addQuestion(UUID dialogid, Message message) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        if(dialog.getClosed()) return null;
        message.setDialog(dialog);
        message.setUser(accountRepository.findOne(message.getUser().getId()));
        message.setType("question");
        Message addedMessage = messageRepository.saveAndFlush(message);
        List<Message> messages = dialog.getMessages();
        messages.add(addedMessage);
        dialog.setMessages(messages);
        dialog.setAnswered(false);
        return dialogRepository.saveAndFlush(dialog);
    }

    @Override
    public Dialog setClosed(UUID dialogid, Boolean closed) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        dialog.setClosed(closed);
        return dialog;
    }

    @Override
    public Boolean delete(UUID id) {
        if(dialogRepository.findOne(id) != null) {
            dialogRepository.delete(id);
            return true;
        } else return false;
    }
}
