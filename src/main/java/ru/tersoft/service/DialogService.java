package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Message;

import java.util.UUID;

public interface DialogService {
    Page<Dialog> getAll(int page, int limit);
    Dialog getById(UUID id);
    Page<Dialog> getByAnswered(int page, int limit);
    Page<Dialog> getByUser(UUID userid, int page, int limit);
    Dialog start(Message message);
    Dialog addAnswer(UUID dialogid, Message message, Boolean closed);
    Dialog addQuestion(UUID dialogid, Message message);
    Dialog setClosed(UUID dialogid, Boolean closed);
    void delete(UUID id);
}
