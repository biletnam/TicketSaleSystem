package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Account;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.service.AccountService;

import java.util.List;
import java.util.UUID;

@Service("AccountService")
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Account> getAll() {
        return (List<Account>)accountRepository.findAll();
    }

    public Account get(UUID id ) {
        return accountRepository.findOne(id);
    }

    public void add(Account account) {
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        account.setPassword(encodedPassword);
        accountRepository.save(account);
    }

    public void delete(UUID id) {
       accountRepository.delete(id);
    }

    public void edit(Account account) {
        Account existingAccount = accountRepository.findOne(account.getId());
        existingAccount.setFirstname(account.getFirstname());
        existingAccount.setLastname(account.getLastname());
        accountRepository.save(existingAccount);
    }
}
