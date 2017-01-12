package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.ErrorResponse;
import ru.tersoft.service.AccountService;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/accounts")
@Api(description = "Work with user accounts", tags = {"Account"})
public class AccountController {
    @Resource(name="AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "Get all accounts data", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Page<Account> getAccounts(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                     @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return accountService.getAll(pageNum, limit);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Create new account", response = Account.class)
    public ResponseEntity<?> add(@RequestBody Account account) {
        if(account != null) {
            account.setAdmin(false);
            try {
                Account addedAccount = accountService.add(account);
                return new ResponseEntity<>(addedAccount, HttpStatus.OK);
            } catch(DataIntegrityViolationException e) {
                return new ResponseEntity<>
                        (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                                "E-mail already in use"),
                                HttpStatus.BAD_REQUEST);
            }
        } else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty account"),
                        HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete account from database by id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = accountService.delete(id);
        if(isDeleted) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Account with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data by id", notes = "Admin access required", response = Account.class)
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        if(account != null) return new ResponseEntity<>(account, HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Account with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data of current user", response = Account.class)
    public ResponseEntity<?> get(Principal principal) {
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        Account account = accountService.get(userid);
        if(account != null) return new ResponseEntity<>(account, HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Account with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/mail/{mail}", method = RequestMethod.GET)
    @ApiOperation(value = "Check user's mail", response = Boolean.class)
    public ResponseEntity<?> checkMail(@PathVariable("mail") String mail) {
        Boolean isFree = accountService.checkMail(mail);
        return new ResponseEntity<>(isFree, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit account data with provided id", response = Account.class)
    public ResponseEntity<?> edit(@RequestBody Account account) {
        Boolean isEdited = accountService.edit(account);
        if(isEdited) return new ResponseEntity<>(accountService.get(account.getId()), HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Account with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Set user's admin flag", notes = "Admin access required", response = Account.class)
    public ResponseEntity<?> makeAdmin(@PathVariable("id") UUID id, @RequestParam boolean admin) {
        Account account = accountService.get(id);
        if(account != null) {
            account.setAdmin(admin);
            accountService.edit(account);
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Account with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }
}
