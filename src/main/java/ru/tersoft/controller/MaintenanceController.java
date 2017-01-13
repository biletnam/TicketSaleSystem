package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Maintenance;
import ru.tersoft.service.MaintenanceService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions/main/")
@Api(description = "Maintenance settings for attractions", tags = {"Maintenance"})
public class MaintenanceController {
    @Resource(name = "MaintenanceService")
    private MaintenanceService maintenanceService;

    @RequestMapping(value = "/{attrid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenance dates")
    public ResponseEntity<?> getMaintenances(@PathVariable("attrid") UUID attractionid) {
        return maintenanceService.getAll(attractionid);
    }

    @RequestMapping(value = "/{attrid}/{date}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenances by date")
    public ResponseEntity<?> getMaintenances
            (@PathVariable("attrid") UUID attractionid,
             @PathVariable("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date today) {
        return maintenanceService.getByDate(today, attractionid);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new maintenance period", notes = "Admin access required", response = Maintenance.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestBody Maintenance maintenance) {
        return maintenanceService.add(maintenance);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit maintenance info", notes = "Admin access required", response = Maintenance.class)
    public ResponseEntity<?> edit(@RequestBody Maintenance maintenance) {
        return maintenanceService.edit(maintenance);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete maintenance", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        return maintenanceService.delete(id);
    }
}
