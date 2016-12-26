package ru.tersoft.service.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final AccountRepository accountRepository;
    private final SessionFactory sessionFactory;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Account findUserByMail(String mail) {
        List<Account> accounts = (List<Account>)accountRepository.findByMail(mail);
        if(accounts.size() != 0) {
            return accounts.get(0);
        }
        else {
            return null;
        }
    }

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, SessionFactory sessionFactory) {
        this.accountRepository = accountRepository;
        this.sessionFactory = sessionFactory;
    }

    public Page<Account> getAll(int pagenum, int limit) {
        return accountRepository.findAll(new PageRequest(pagenum, limit));
    }

    public Account get(UUID id) {
        return accountRepository.findOne(id);
    }

    public Account add(Account account) {
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        account.setPassword(encodedPassword);
        int avatarNumber = (account.getFirstname().length() + account.getLastname().length()) % 10;
        account.setAvatar("/images/avatars/identicon"+avatarNumber+".png");
        if(account.isEnabled() == null) account.setEnabled(true);
        return accountRepository.saveAndFlush(account);
    }

    public Boolean delete(UUID id) {
        Account account = accountRepository.findOne(id);
        if(account == null) return false;
        else {
            accountRepository.delete(id);
            return true;
        }
    }

    public Boolean edit(Account account) {
        if(account == null) return false;
        if(account.getId() == null) return false;
        Account existingAccount = accountRepository.findOne(account.getId());
        if(existingAccount == null) return false;
        if(account.getFirstname() != null && !account.getFirstname().isEmpty())
            existingAccount.setFirstname(account.getFirstname());
        if(account.getLastname() != null && !account.getLastname().isEmpty())
            existingAccount.setLastname(account.getLastname());
        if(account.getPassword() != null && !account.getPassword().isEmpty())
            existingAccount.setPassword(account.getPassword());
        if(account.getBirthdate() != null)
            existingAccount.setBirthdate(account.getBirthdate());
        if(account.isEnabled() != null) {
            existingAccount.setEnabled(account.isEnabled());
        }
        accountRepository.save(existingAccount);
        return true;
    }

    @Override
    public Boolean checkMail(String mail) {
        List<Account> accountList = (List<Account>)accountRepository.findByMail(mail);
        return accountList.size() == 0;
    }
}
