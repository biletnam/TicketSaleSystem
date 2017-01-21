package ru.tersoft.ticketsale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.service.AccountService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/accounts")
@Api(description = "Work with user accounts", tags = {"Account"})
public class AccountController {
    @Resource(name = "AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "Get all accounts data", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getAccounts(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                         @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return accountService.getAll(pageNum, limit);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Create new account", response = Account.class)
    public ResponseEntity<?> registerNewAccount(@RequestBody Account account) {
        return accountService.add(account);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete account from database by id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@PathVariable("id") UUID id) {
        return accountService.delete(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data by id", notes = "Admin access required", response = Account.class)
    public ResponseEntity<?> getAccountById(@PathVariable("id") UUID id) {
        return accountService.get(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Get account data of current user", response = Account.class)
    public ResponseEntity<?> getAccount(Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        return accountService.get(userid);
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
    @ApiOperation(value = "Edit account data", response = Account.class, notes = "You don't need to pass account id here")
    public ResponseEntity<?> editAccount(@RequestBody Account account, Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        account.setId(userid);
        return accountService.edit(account);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Change user's flags", notes = "Admin access required", response = Account.class)
    public ResponseEntity<?> changeFlags(@PathVariable("id") UUID id,
                                         @RequestParam(required = false) Boolean admin,
                                         @RequestParam(required = false) Boolean enabled) {
        return accountService.changeFlags(id, admin, enabled);
    }
}
