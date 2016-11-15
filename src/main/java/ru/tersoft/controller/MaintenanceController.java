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
import ru.tersoft.entity.Attraction;
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of maintenance dates by attraction id or date")
    public List<Maintenance> getMaintenances
            (@RequestParam(value = "attrid", required = false) UUID attractionid,
             @RequestParam(value = "date", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date today) {
        if(today != null) {
            return (List<Maintenance>) maintenanceService.getAll(today);
        } else if(attractionid != null) {
            return (List<Maintenance>) maintenanceService.getAll(attractionid);
        }
        else throw new RequestRejectedException("You must pass attraction id or date");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new maintenance period", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestParam UUID attrid,
                                 @RequestParam String reason,
                                 @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startdate,
                                 @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date enddate) {
        Maintenance maintenance = new Maintenance();
        Attraction attraction = new Attraction();
        attraction.setId(attrid);
        maintenance.setAttraction(attraction);
        maintenance.setReason(reason);
        maintenance.setStartdate(startdate);
        maintenance.setEnddate(enddate);
        maintenanceService.add(maintenance);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get maintenance info by id")
    public Maintenance get(@PathVariable("id") UUID id) {
        Maintenance maintenance = maintenanceService.get(id);
        return maintenance;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit maintenance info", notes = "Admin access required")
    public ResponseEntity<Maintenance> edit(@PathVariable("id") UUID id,
                                            @RequestParam(required = false) String reason,
                                            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startdate,
                                            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date enddate) {
        Maintenance maintenance = new Maintenance();
        maintenance.setId(id);
        maintenance.setReason(reason);
        maintenance.setStartdate(startdate);
        maintenance.setEnddate(enddate);
        if(maintenance.getId() != null)
            maintenanceService.edit(maintenance);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(maintenanceService.get(id), HttpStatus.OK);
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
