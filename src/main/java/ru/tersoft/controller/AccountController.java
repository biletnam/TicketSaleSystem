package ru.tersoft.controller;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Account;
import ru.tersoft.service.AccountService;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("accounts")
public class AccountController {
    private static final Logger LOG = Logger.getLogger(AccountController.class);
    @Resource(name="AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Page<Account> getAccounts(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                     @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        Page<Account> accounts = accountService.getAll(pageNum, limit);
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
