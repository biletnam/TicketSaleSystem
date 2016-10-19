package ru.tersoft.service;

import ru.tersoft.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    List<Account> getAll();
    Account get(UUID id);
    void add(Account account);
    void delete(UUID id);
    void edit(Account account);
}
