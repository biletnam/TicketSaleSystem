package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Question;
import ru.tersoft.repository.QuestionRepository;
import ru.tersoft.service.QuestionService;

import java.util.UUID;

@Service("QuestionService")
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Page<Question> getAll(int page, int limit) {
        return questionRepository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Question get(UUID id) {
        return questionRepository.findOne(id);
    }

    @Override
    public Question add(Question question) {
        return questionRepository.saveAndFlush(question);
    }

    @Override
    public void delete(UUID id) {
        questionRepository.delete(id);
    }
}
