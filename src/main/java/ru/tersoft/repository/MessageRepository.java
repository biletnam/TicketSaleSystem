package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Message;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
