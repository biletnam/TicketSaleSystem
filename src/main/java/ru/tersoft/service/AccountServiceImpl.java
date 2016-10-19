package ru.tersoft.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Account;

import java.util.List;

@Service("AccountService")
@Transactional
public interface AccountServiceImpl {

    List<Account> getAll();
    Account get(Long id);
    void add(Account account);
    void delete(Long id);
    void edit(Account account);
}
