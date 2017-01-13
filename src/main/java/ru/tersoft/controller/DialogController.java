package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Message;
import ru.tersoft.service.AccountService;
import ru.tersoft.service.DialogService;
import ru.tersoft.utils.ResponseFactory;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/dialogs")
@Api(description = "Dialogs between users and administrators", tags = {"Dialog"})
public class DialogController {
    @Resource(name="DialogService")
    private DialogService dialogService;

    @Resource(name="AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get all dialogs", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Page<Dialog> getDialogs(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                    @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getAll(pageNum, limit);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/waiting", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs by answered flag", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Page<Dialog> getByAnswered(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                   @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getByAnswered(pageNum, limit);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs by user id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getByUser(@PathVariable("id") UUID userid,
                                   @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                   @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        Account account = accountService.get(userid);
        if(account != null)
            return ResponseFactory.createResponse(dialogService.getByUser(userid, pageNum, limit));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Account with such id was not found");
    }

    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getByCurrentUser(Principal principal,
                                  @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                  @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {

        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        return ResponseFactory.createResponse(dialogService.getByUser(userid, pageNum, limit));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialog by id", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getById(@PathVariable("id") UUID dialogid) {
        Dialog dialog = dialogService.getById(dialogid);
        if(dialog != null)
            return ResponseFactory.createResponse(dialog);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Open new dialog", notes = "You don't need to pass account id here", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> startDialog(@RequestBody Message message, @RequestParam String title, Principal principal) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        if(message != null && title != null && !title.isEmpty()) {
            message.setUser(accountService.findUserByMail(principal.getName()));
            Dialog dialog = dialogService.start(message, title);
            return ResponseFactory.createResponse(dialog);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty message");
        }
    }

    @RequestMapping(value = "/{id}/addquestion", method = RequestMethod.POST)
    @ApiOperation(value = "Post new user question", notes = "You don't need to pass account id here", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> postQuestion(@PathVariable("id") UUID dialogid, @RequestBody Message message, Principal principal) {
        Dialog dialog = dialogService.getById(dialogid);
        if(dialog != null) {
            if (message != null) {
                message.setUser(accountService.findUserByMail(principal.getName()));
                dialog = dialogService.addQuestion(dialogid, message);
                if (dialog != null)
                    return ResponseFactory.createResponse(dialog);
                else
                    return ResponseFactory.createErrorResponse(HttpStatus.LOCKED, "Such dialog was closed");
            } else {
                return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty message");
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}/addanswer", method = RequestMethod.POST)
    @ApiOperation(value = "Post new admin answer", notes = "Admin access required / You don't need to pass account id here", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> postAnswer(@PathVariable("id") UUID dialogid, @RequestBody Message message, @RequestParam(required = false) Boolean closed, Principal principal) {
        Dialog dialog = dialogService.getById(dialogid);
        if(dialog != null) {
            if (message != null) {
                message.setUser(accountService.findUserByMail(principal.getName()));
                dialog = dialogService.addAnswer(dialogid, message, closed);
                return ResponseFactory.createResponse(dialog);
            } else {
                return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty message");
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Open/close dialog", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> setClosed(@PathVariable("id") UUID dialogid, @RequestParam Boolean closed) {
        Dialog dialog = dialogService.getById(dialogid);
        if(dialog != null) {
            dialog = dialogService.setClosed(dialogid, closed);
            return ResponseFactory.createResponse(dialog);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete dialog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> delete(@PathVariable("id") UUID dialogid) {
        Boolean isDeleted = dialogService.delete(dialogid);
        if(isDeleted)
            return ResponseFactory.createResponse();
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Dialog with such id was not found");
    }
}
