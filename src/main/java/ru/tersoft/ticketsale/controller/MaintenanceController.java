package ru.tersoft.ticketsale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.ticketsale.entity.Maintenance;
import ru.tersoft.ticketsale.service.MaintenanceService;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions/main/")
@Api(description = "Maintenance settings for attractions", tags = {"Maintenance"})
public class MaintenanceController {
    @Resource(name = "MaintenanceService")
    private MaintenanceService maintenanceService;

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