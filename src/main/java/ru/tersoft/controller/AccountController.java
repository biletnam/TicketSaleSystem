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
import ru.tersoft.service.AccountService;
import ru.tersoft.utils.ResponseFactory;

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
                return ResponseFactory.createResponse(addedAccount);
            } catch(DataIntegrityViolationException e) {
                return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "E-mail already in use");
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty account");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete account from database by id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = accountService.delete(id);
        if(isDeleted)
            return ResponseFactory.createResponse();
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data by id", notes = "Admin access required", response = Account.class)
    public ResponseEntity<?> getById(@PathVariable("id") UUID id) {
        Account account = accountService.get(id);
        if(account != null)
            return ResponseFactory.createResponse(account);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data of current user", response = Account.class)
    public ResponseEntity<?> get(Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        Account account = accountService.get(userid);
        if(account != null)
            return ResponseFactory.createResponse(account);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ApiOperation(value = "Check user's mail", response = Boolean.class)
    public ResponseEntity<?> checkMail(@RequestParam("mail") String mail) {
        return ResponseFactory.createResponse(accountService.checkMail(mail));
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit account data with provided id", response = Account.class)
    public ResponseEntity<?> edit(@RequestBody Account account) {
        Boolean isEdited = accountService.edit(account);
        if(isEdited)
            return ResponseFactory.createResponse(accountService.get(account.getId()));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
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
            return ResponseFactory.createResponse(account);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
        }
    }
}
