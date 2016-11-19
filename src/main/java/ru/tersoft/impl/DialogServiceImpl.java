package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Answer;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Question;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.repository.AnswerRepository;
import ru.tersoft.repository.DialogRepository;
import ru.tersoft.repository.QuestionRepository;
import ru.tersoft.service.DialogService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("DialogService")
@Transactional
public class DialogServiceImpl implements DialogService {
    private final DialogRepository dialogRepository;
    private final AccountRepository accountRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public DialogServiceImpl(DialogRepository dialogRepository, AccountRepository accountRepository, AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.dialogRepository = dialogRepository;
        this.accountRepository = accountRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Page<Dialog> getAll(int page, int limit) {
        return dialogRepository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Dialog get(UUID id) {
        return dialogRepository.findOne(id);
    }

    @Override
    public Iterable<Dialog> getByUser(UUID userid) {
        return dialogRepository.findByUser(accountRepository.findOne(userid));
    }

    @Override
    public Dialog start(Question question, UUID userid) {
        Question addedQuestion = questionRepository.saveAndFlush(question);
        Dialog dialog = new Dialog();
        List<Question> questions = new ArrayList<>();
        questions.add(addedQuestion);
        dialog.setClosed(false);
        dialog.setQuestions(questions);
        dialog.setUser(accountRepository.findOne(userid));
        dialog = dialogRepository.saveAndFlush(dialog);
        addedQuestion.setDialog(dialog);
        questionRepository.saveAndFlush(addedQuestion);
        return dialog;
    }

    @Override
    public Dialog addAnswer(UUID dialogid, Answer answer, Boolean closed) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        answer.setDialog(dialog);
        answer.setAdmin(accountRepository.findOne(answer.getAdmin().getId()));
        Answer addedAnswer = answerRepository.saveAndFlush(answer);
        List<Answer> answers = dialog.getAnswers();
        answers.add(addedAnswer);
        dialog.setAnswers(answers);
        if(closed != null) {
            if(closed) dialog.setClosed(true);
        }
        return dialogRepository.saveAndFlush(dialog);
    }

    @Override
    public Dialog addQuestion(UUID dialogid, Question question) {
        Dialog dialog = dialogRepository.findOne(dialogid);
        if(dialog.getClosed()) return null;
        question.setDialog(dialog);
        Question addedQuestion = questionRepository.saveAndFlush(question);
        List<Question> questions = dialog.getQuestions();
        questions.add(addedQuestion);
        dialog.setQuestions(questions);
        return dialogRepository.saveAndFlush(dialog);
    }

    @Override
    public void delete(UUID id) {
        dialogRepository.delete(id);
    }
}
