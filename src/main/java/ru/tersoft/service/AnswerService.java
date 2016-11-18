package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Answer;

import java.util.UUID;

public interface AnswerService {
    Page<Answer> getAll(int page, int limit);
    Answer get(UUID id);
    Iterable<Answer> getByAdmin(UUID adminid);
    Answer add(Answer answer);
    void delete(UUID id);
}
