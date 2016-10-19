package ru.tersoft.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.tersoft.entity.Account;
import ru.tersoft.service.AccountServiceImpl;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {

    @Resource(name="AccountServiceImpl")
    private AccountServiceImpl accountService;

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
    public int delete(@PathVariable("id") Long id) {
        accountService.delete(id);
        return 200;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account get(@PathVariable("id") Long id) {
        Account account = accountService.get(id);
        return account;
    }
}
