package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Account;
import ru.tersoft.repository.AccountRepository;
import ru.tersoft.service.AccountService;

import java.util.UUID;

@Service("AccountService")
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Page<Account> getAll(int pagenum, int limit) {
        final Page<Account> page = accountRepository.findAll(new PageRequest(pagenum, limit));
        return page;
    }

    public Account get(UUID id) {
        return accountRepository.findOne(id);
    }

    public void add(Account account) {
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        account.setPassword(encodedPassword);
        if(account.isEnabled() == null) account.setEnabled(true);
        if(account.isAdmin() == null) account.setAdmin(false);
        accountRepository.save(account);
    }

    public void delete(UUID id) {
       accountRepository.delete(id);
    }

    public void edit(Account account) {
        Account existingAccount = accountRepository.findOne(account.getId());
        if(account.getFirstname() != null)
            existingAccount.setFirstname(account.getFirstname());
        if(account.getLastname() != null)
            existingAccount.setLastname(account.getLastname());
        if(account.getMail() != null)
            existingAccount.setMail(account.getMail());
        if(account.getPassword() != null)
            existingAccount.setPassword(account.getPassword());
        if(account.getBirthdate() != null)
            existingAccount.setBirthdate(account.getBirthdate());
        if(account.isEnabled() != null) {
            existingAccount.setEnabled(account.isEnabled());
        }
        if(account.isAdmin() != null) {
            existingAccount.setAdmin(account.isAdmin());
        }
        accountRepository.save(existingAccount);
    }
}
