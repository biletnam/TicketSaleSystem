package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Maintenance;
import ru.tersoft.service.AttractionService;
import ru.tersoft.service.MaintenanceService;
import ru.tersoft.utils.ResponseFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions/main/")
@Api(description = "Maintenance settings for attractions", tags = {"Maintenance"})
public class MaintenanceController {
    @Resource(name = "MaintenanceService")
    private MaintenanceService maintenanceService;
    @Resource(name = "AttractionService")
    private AttractionService attractionService;

    @RequestMapping(value = "/{attrid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenance dates")
    public ResponseEntity<?> getMaintenances(@PathVariable("attrid") UUID attractionid) {
        if(attractionService.get(attractionid) == null)
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        if(attractionid != null)
            return ResponseFactory.createResponse(maintenanceService.getAll(attractionid));
        else
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Attraction id was not passed");
    }

    @RequestMapping(value = "/{attrid}/{date}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenances by date")
    public ResponseEntity<?> getMaintenances
            (@PathVariable("attrid") UUID attractionid,
             @PathVariable("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date today) {
        if(today != null && attractionid != null) {
            List<Maintenance> maintenances = (List<Maintenance>)maintenanceService.getByDate(today, attractionid);
            if(maintenances != null)
                return ResponseFactory.createResponse(maintenances);
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Date or attraction id was not passed");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new maintenance period", notes = "Admin access required", response = Maintenance.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestBody Maintenance maintenance) {
        Maintenance addedMaintenance = maintenanceService.add(maintenance);
        if(addedMaintenance != null)
            return ResponseFactory.createResponse(addedMaintenance);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit maintenance info", notes = "Admin access required", response = Maintenance.class)
    public ResponseEntity<?> edit(@RequestBody Maintenance maintenance) {
        if(maintenance != null) {
            Boolean isEdited = maintenanceService.edit(maintenance);
            if(isEdited)
                return ResponseFactory.createResponse(maintenanceService.get(maintenance.getId()));
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty maintenance");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete maintenance", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = maintenanceService.delete(id);
        if(isDeleted)
            return ResponseFactory.createResponse();
        else
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
    }
}
