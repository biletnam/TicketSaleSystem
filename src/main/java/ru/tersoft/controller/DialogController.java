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

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("dialogs")
@Api(description = "Dialogs between users and administrators", tags = {"Dialog"})
public class DialogController {
    @Resource(name="DialogService")
    private DialogService dialogService;

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

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Open new dialog")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> startDialog(@RequestBody Message message) {
        if(message != null) {
            Dialog dialog = dialogService.start(message);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{dialogid}", method = RequestMethod.POST)
    @ApiOperation(value = "Post new user question")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> postQuestion(@PathVariable("dialogid") UUID dialogid, @RequestBody Message message) {
        if(message != null) {
            Dialog dialog = dialogService.addQuestion(dialogid, message);
            if(dialog != null)
                return new ResponseEntity<>(dialog, HttpStatus.OK);
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/answers/", method = RequestMethod.POST)
    @ApiOperation(value = "Post new admin answer", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> postAnswer(@RequestParam UUID dialogid, @RequestBody Message message, @RequestParam(required = false) Boolean closed) {
        if(message != null) {
            Dialog dialog = dialogService.addAnswer(dialogid, message, closed);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
