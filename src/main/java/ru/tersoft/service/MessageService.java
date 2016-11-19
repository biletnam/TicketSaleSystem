package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Message;

import java.util.UUID;

public interface MessageService {
    Page<Message> getAll(int page, int limit);
    Message getById(UUID id);
    Iterable<Message> getByUser(UUID userid);
    Message add(Message question);
    void delete(UUID id);
}
