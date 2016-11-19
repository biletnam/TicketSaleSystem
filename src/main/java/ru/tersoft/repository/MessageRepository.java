package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.Message;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Iterable<Message> findByUser(Account account);
}
