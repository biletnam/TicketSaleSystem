package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.Answer;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Iterable<Answer> findByAdmin(Account account);
}
