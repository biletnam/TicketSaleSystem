package ru.tersoft.ticketsale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.ticketsale.entity.Dialog;
import ru.tersoft.ticketsale.entity.Message;
import ru.tersoft.ticketsale.service.AccountService;
import ru.tersoft.ticketsale.service.DialogService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/dialogs")
@Api(description = "Dialogs between users and administrators", tags = {"Dialog"})
public class DialogController {
    @Resource(name = "DialogService")
    private DialogService dialogService;
    @Resource(name = "AccountService")
    private AccountService accountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/waiting", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs waiting for answer", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getWaitingDialogs(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                               @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getByAnswered(pageNum, limit);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs by user id", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getDialogsByUser(@PathVariable("id") UUID userid,
                                              @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                              @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getByUser(userid, pageNum, limit);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    @ApiOperation(value = "Get all open dialogs", notes = "Admin access required", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getOpenDialogs(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                              @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getOpenDialogs(pageNum, limit);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getDialogs(Principal principal,
                                        @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                        @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        if(principal == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong or empty access token");
        UUID userid = accountService.findUserByMail(principal.getName()).getId();
        return dialogService.getByUser(userid, pageNum, limit);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialog by id", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> getDialogById(@PathVariable("id") UUID dialogid) {
        return dialogService.getById(dialogid);
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
            return dialogService.start(message, title);
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
        if (message != null) {
            message.setUser(accountService.findUserByMail(principal.getName()));
            return dialogService.addQuestion(dialogid, message);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty message");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}/addanswer", method = RequestMethod.POST)
    @ApiOperation(value = "Post new admin answer", notes = "Admin access required / You don't need to pass account id here", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> postAnswer(@PathVariable("id") UUID dialogid, @RequestBody Message message, @RequestParam(required = false) Boolean closed, Principal principal) {
        if (message != null) {
            message.setUser(accountService.findUserByMail(principal.getName()));
            return dialogService.addAnswer(dialogid, message, closed);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty message");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Open/close dialog", response = Dialog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> setClosed(@PathVariable("id") UUID dialogid, @RequestParam Boolean closed) {
        return dialogService.setClosed(dialogid, closed);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete dialog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> deleteDialog(@PathVariable("id") UUID dialogid) {
        return dialogService.delete(dialogid);
    }
}
