package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.Account;

import java.util.UUID;

public interface AccountService {
    ResponseEntity<?> getAll(int page, int limit);
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> add(Account account);
    Account findUserByMail(String mail);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(Account account);
    Boolean checkMail(String mail);
    ResponseEntity<?> changeFlags(UUID id, Boolean admin, Boolean enabled);
}
