package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Message;

import java.util.UUID;

public interface DialogService {
    Page<Dialog> getAll(int page, int limit);
    Dialog getById(UUID id);
    Iterable<Dialog> getByAnswered(Boolean answered);
    Dialog start(Message message);
    Dialog addAnswer(UUID dialogid, Message message, Boolean closed);
    Dialog addQuestion(UUID dialogid, Message message);
    void delete(UUID id);
}
