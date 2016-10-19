package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Account;
import ru.tersoft.repository.AccountRepository;

import java.util.List;

@Service("AccountService")
@Transactional
public class AccountServiceImpl implements ru.tersoft.service.AccountServiceImpl {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAll() {
        return (List<Account>)accountRepository.findAll();
    }

    public Account get( Long id ) {
        return accountRepository.findOne(id);
    }

    public void add(Account account) {
        accountRepository.save(account);
    }

    public void delete(Long id) {
       accountRepository.delete(id);
    }

    public void edit(Account account) {
        Account existingAccount = accountRepository.findOne(account.getId());
        existingAccount.setFirstname(account.getFirstname());
        existingAccount.setLastname(account.getLastname());
        accountRepository.save(existingAccount);
    }
}
