package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Answer;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Question;

import java.util.UUID;

public interface DialogService {
    Page<Dialog> getAll(int page, int limit);
    Dialog get(UUID id);
    Iterable<Dialog> getByUser(UUID userid);
    Dialog start(Question question, UUID userid);
    Dialog addAnswer(UUID dialogid, Answer answer, Boolean closed);
    Dialog addQuestion(UUID dialogid, Question question);
    void delete(UUID id);
}
