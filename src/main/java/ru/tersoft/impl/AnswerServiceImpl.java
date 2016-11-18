package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Answer;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.repository.AnswerRepository;
import ru.tersoft.service.AnswerService;

import java.util.UUID;

@Service("AnswerService")
@Transactional
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository, AccountRepository accountRepository) {
        this.answerRepository = answerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Answer> getAll(int page, int limit) {
        return answerRepository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Answer get(UUID id) {
        return answerRepository.findOne(id);
    }

    @Override
    public Iterable<Answer> getByAdmin(UUID adminid) {
        return answerRepository.findByAdmin(accountRepository.findOne(adminid));
    }

    @Override
    public Answer add(Answer answer) {
        return answerRepository.saveAndFlush(answer);
    }

    @Override
    public void delete(UUID id) {
        answerRepository.delete(id);
    }
}
