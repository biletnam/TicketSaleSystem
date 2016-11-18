package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Question;

import java.util.UUID;

public interface QuestionService {
    Page<Question> getAll(int page, int limit);
    Question get(UUID id);
    Question add(Question question);
    void delete(UUID id);
}
