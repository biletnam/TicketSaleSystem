package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Account;

import java.util.UUID;

public interface AccountService {
    Page<Account> getAll(int page, int limit);
    Account get(UUID id);
    Account add(Account account);
    void delete(UUID id);
    void edit(Account account);
}
