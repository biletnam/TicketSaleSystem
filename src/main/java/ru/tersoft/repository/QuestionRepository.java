package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Question;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
