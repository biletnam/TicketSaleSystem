package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.ticketsale.entity.Message;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
