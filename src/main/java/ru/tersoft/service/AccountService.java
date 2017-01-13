package ru.tersoft.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.entity.Account;

import java.util.UUID;

public interface AccountService {
    ResponseEntity<?> getAll(int page, int limit);
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> add(Account account);
    Account findUserByMail(String mail);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(Account account);
    Boolean checkMail(String mail);
}
