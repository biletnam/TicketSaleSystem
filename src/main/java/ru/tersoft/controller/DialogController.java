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
import ru.tersoft.entity.Answer;
import ru.tersoft.entity.Dialog;
import ru.tersoft.entity.Question;
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
    public ResponseEntity<Dialog> startDialog(@RequestParam UUID userid, @RequestBody Question question) {
        if(question != null) {
            Dialog dialog = dialogService.start(question, userid);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/answers/", method = RequestMethod.POST)
    @ApiOperation(value = "Post new admin answer")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Dialog> postAnswer(@RequestParam UUID dialogid, @RequestBody Answer answer) {
        if(answer != null) {
            Dialog dialog = dialogService.addAnswer(dialogid, answer);
            return new ResponseEntity<>(dialog, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
