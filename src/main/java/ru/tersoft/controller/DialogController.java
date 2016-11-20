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
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Message;
import ru.tersoft.service.DialogService;
import ru.tersoft.service.UserService;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("dialogs")
@Api(description = "Dialogs between users and administrators", tags = {"Dialog"})
public class DialogController {
    @Resource(name="DialogService")
    private DialogService dialogService;

    @Resource(name="UserService")
    private UserService userService;

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
    public Page<Dialog> getByUser(@PathVariable("id") UUID userid,
                                   @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                   @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        return dialogService.getByUser(userid, pageNum, limit);
    }

    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialogs for current user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Page<Dialog> getByCurrentUser(Principal principal,
                                  @RequestParam(value = "page", defaultValue = "0", required = false) int pageNum,
                                  @RequestParam(value = "limit", defaultValue = "20", required = false) int limit) {
        UUID userid = userService.findUserByMail(principal.getName()).getId();
        return dialogService.getByUser(userid, pageNum, limit);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get dialog by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public Dialog getById(@PathVariable("id") UUID dialogid) {
        return dialogService.getById(dialogid);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Open new dialog", notes = "You don't need to pass account id here")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> startDialog(@RequestBody Message message, Principal principal) {
        if(message != null) {
            message.setUser(userService.findUserByMail(principal.getName()));
            Dialog dialog = dialogService.start(message);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}/addquestion", method = RequestMethod.POST)
    @ApiOperation(value = "Post new user question", notes = "You don't need to pass account id here")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> postQuestion(@PathVariable("id") UUID dialogid, @RequestBody Message message, Principal principal) {
        if(message != null) {
            message.setUser(userService.findUserByMail(principal.getName()));
            Dialog dialog = dialogService.addQuestion(dialogid, message);
            if(dialog != null)
                return new ResponseEntity<>(dialog, HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}/addanswer", method = RequestMethod.POST)
    @ApiOperation(value = "Post new admin answer", notes = "Admin access required / You don't need to pass account id here")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> postAnswer(@PathVariable("id") UUID dialogid, @RequestBody Message message, @RequestParam(required = false) Boolean closed, Principal principal) {
        if(message != null) {
            message.setUser(userService.findUserByMail(principal.getName()));
            Dialog dialog = dialogService.addAnswer(dialogid, message, closed);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Open/close dialog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> setClosed(@PathVariable("id") UUID dialogid, @RequestParam Boolean closed) {
        Dialog dialog = dialogService.setClosed(dialogid, closed);
        return new ResponseEntity<>(dialog, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete dialog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> delete(@PathVariable("id") UUID dialogid) {
        dialogService.delete(dialogid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
