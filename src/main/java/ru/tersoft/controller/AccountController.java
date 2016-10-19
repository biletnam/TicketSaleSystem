package ru.tersoft.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.tersoft.entity.Account;
import ru.tersoft.service.AccountService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("accounts")
public class AccountController {

    @Resource(name="AccountService")
    private AccountService accountService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Account> getAccounts() {
        List<Account> accounts = accountService.getAll();
        return accounts;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(Account account) {
        accountService.add(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        accountService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account get(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        return account;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResponseEntity<?> edit(Account account) {
        accountService.edit(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
