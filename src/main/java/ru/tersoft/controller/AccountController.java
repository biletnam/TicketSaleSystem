package ru.tersoft.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.domain.Account;
import ru.tersoft.service.AccountService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {

    @Resource(name="AccountService")
    private AccountService accountService;

    //@PreAuthorize("#oauth2.clientHasRole('ROLE_USER')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Account> getAccounts() {
        // Retrieve all persons by delegating the call to PersonService
        List<Account> accounts = accountService.getAll();
        return accounts;
    }

    //@PreAuthorize("#oauth2.clientHasRole('ROLE_USER')")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public int add(Account account) {
        accountService.add(account);
        return 200;
    }

    //@PreAuthorize("#oauth2.clientHasRole('ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable("id") Integer id) {
        // Call PersonService to do the actual deleting
        accountService.delete(id);
        return 200;
    }

    //@PreAuthorize("#oauth2.clientHasRole('ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account get(@PathVariable("id") Integer id) {
        Account account = accountService.get(id);
        return account;
    }
}
