package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Message;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.repository.MessageRepository;
import ru.tersoft.service.MessageService;

import java.util.UUID;

@Service("MessageService")
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Message> getAll(int page, int limit) {
        return messageRepository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Message getById(UUID id) {
        return messageRepository.findOne(id);
    }

    @Override
    public Iterable<Message> getByUser(UUID userid) {
        return messageRepository.findByUser(accountRepository.findOne(userid));
    }

    @Override
    public Message add(Message message) {
        return messageRepository.saveAndFlush(message);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}
