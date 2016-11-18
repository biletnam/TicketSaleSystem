package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.*;
import ru.tersoft.entity.Maintenance;
import ru.tersoft.service.MaintenanceService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("attractions/main/")
@Api(description = "Maintenance settings for attractions", tags = {"Maintenance"})
public class MaintenanceController {
    @Resource(name = "MaintenanceService")
    private MaintenanceService maintenanceService;

    @RequestMapping(value = "/{attrid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenance dates by attraction id")
    public List<Maintenance> getMaintenances
            (@PathVariable("attrid") UUID attractionid) {
        if(attractionid != null) {
            return (List<Maintenance>) maintenanceService.getAll(attractionid);
        }
        else throw new RequestRejectedException("You must pass attraction id");
    }

    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenance dates by date")
    public List<Maintenance> getMaintenances
            (@PathVariable("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date today) {
        if(today != null) {
            return (List<Maintenance>) maintenanceService.getAll(today);
        }
        else throw new RequestRejectedException("You must pass date");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new maintenance period", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<Maintenance> add(@RequestBody Maintenance maintenance) {
        Maintenance addedMaintenance = maintenanceService.add(maintenance);
        return new ResponseEntity<>(addedMaintenance, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit maintenance info", notes = "Admin access required")
    public ResponseEntity<Maintenance> edit(@RequestBody Maintenance maintenance) {
        if(maintenance.getId() != null)
            maintenanceService.edit(maintenance);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(maintenanceService.get(maintenance.getId()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete maintenance", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        maintenanceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
