package ru.tersoft.ticketsale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.service.AttractionService;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions")
@Api(description = "Work with attractions", tags = {"Attraction"})
public class AttractionController {
    @Resource(name = "AttractionService")
    private AttractionService attractionService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of attractions")
    public ResponseEntity<?> getAttractions() {
        return attractionService.getAll();
    }

    @RequestMapping(value = "/cat/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of attractions by category id")
    public ResponseEntity<?> getByCategory(@PathVariable("id") UUID id) {
        return attractionService.getByCategory(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new attraction", notes = "Admin access required", response = Attraction.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestPart(value = "name") String name,
                                 @RequestPart(value = "description") String description,
                                 @RequestPart(value = "cat", required = false) String cat,
                                 @RequestPart(value = "price") String price,
                                 @RequestPart(value = "maintenance", required = false) Boolean maintenance,
                                 @RequestPart(value = "image") MultipartFile image) {
        return attractionService.add(name, description, cat, price, maintenance, image);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete attraction", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        return attractionService.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get attraction by id", response = Attraction.class)
    public ResponseEntity<?> get(@PathVariable("id") UUID id) {
        return attractionService.get(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit attraction info", notes = "Admin access required", response = Attraction.class)
    public ResponseEntity<?> edit(@PathVariable("id") UUID id,
                                  @RequestPart(value = "name", required = false) String name,
                                  @RequestPart(value = "description", required = false) String description,
                                  @RequestPart(value = "cat", required = false) String cat,
                                  @RequestPart(value = "price", required = false) String price,
                                  @RequestPart(value = "maintenance", required = false) Boolean maintenance,
                                  @RequestPart(value = "image", required = false) MultipartFile image) {
        return attractionService.edit(id, name, description, cat, price, maintenance, image);
    }
}
