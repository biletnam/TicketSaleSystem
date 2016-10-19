package ru.tersoft.controller;

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
    public int add(Account account) {
        accountService.add(account);
        return 200;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable("id") UUID id) {
        accountService.delete(id);
        return 200;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account get(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        return account;
    }
}
