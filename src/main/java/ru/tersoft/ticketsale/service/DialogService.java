package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.Message;

import java.util.UUID;

public interface DialogService {
    ResponseEntity<?> getAll(int page, int limit);
    ResponseEntity<?> getById(UUID id);
    ResponseEntity<?> getByAnswered(int page, int limit);
    ResponseEntity<?> getByUser(UUID userid, int page, int limit);
    ResponseEntity<?> start(Message message, String title);
    ResponseEntity<?> addAnswer(UUID dialogid, Message message, Boolean closed);
    ResponseEntity<?> addQuestion(UUID dialogid, Message message);
    ResponseEntity<?> setClosed(UUID dialogid, Boolean closed);
    ResponseEntity<?> delete(UUID id);
}
