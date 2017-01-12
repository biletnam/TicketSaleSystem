package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Category;
import ru.tersoft.entity.ErrorResponse;
import ru.tersoft.service.AttractionService;
import ru.tersoft.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/attractions")
@Api(description = "Work with attractions", tags = {"Attraction"})
public class AttractionController {
    @Resource(name="AttractionService")
    private AttractionService attractionService;
    @Resource(name="CategoryService")
    private CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of attractions")
    public List<Attraction> getAttractions() {
        return (List<Attraction>)attractionService.getAll();
    }

    @RequestMapping(value = "/cat/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of attractions by category id")
    public ResponseEntity<?> getByCategory(@PathVariable("id") UUID id) {
        if(id != null) {
            if(attractionService.getByCategory(id) == null) {
                return new ResponseEntity<>
                        (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                                "Category with such id was not found"),
                                HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>((List<Attraction>)attractionService.getByCategory(id), HttpStatus.OK);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Category id was not passed"),
                        HttpStatus.BAD_REQUEST);
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
        Attraction attraction = new Attraction();
        if(cat != null) {
            Category category = categoryService.get(UUID.fromString(cat));
            if (category == null) return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                            "Category with such id was not found"),
                            HttpStatus.NOT_FOUND);
            attraction.setCategory(category);
        }
        attraction.setDescription(description);
        Float floatPrice = null;
        try {
            if(price != null)
                floatPrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                            "Wrong format of price field"),
                            HttpStatus.BAD_REQUEST);
        }
        attraction.setPrice(floatPrice);
        attraction.setName(name);
        if(maintenance != null)
            attraction.setMaintenance(maintenance);
        if(image != null)
            attraction = attractionService.saveImage(attraction, image);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Image was not selected"),
                        HttpStatus.BAD_REQUEST);
        if(attraction != null) {
            Attraction addedAttraction = attractionService.add(attraction);
            return new ResponseEntity<>(addedAttraction, HttpStatus.OK);
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty attraction"),
                        HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete attraction", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        Boolean isDeleted = attractionService.delete(id);
        if(isDeleted) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Attraction with such id was not found"),
                        HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get attraction by id", response = Attraction.class)
    public ResponseEntity<?> get(@PathVariable("id") UUID id) {
        Attraction attraction = attractionService.get(id);
        if(attraction == null) return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                        "Attraction with such id was not found"),
                HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(attraction, HttpStatus.OK);
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
        Attraction attraction = new Attraction();
        attraction.setId(id);
        attraction.setDescription(description);
        if(cat != null) {
            Category category = categoryService.get(UUID.fromString(cat));
            if (category == null) {
                return new ResponseEntity<>
                        (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                                "Category with such id was not found"),
                                HttpStatus.NOT_FOUND);
            }
            attraction.setCategory(category);
        }
        Float floatPrice = null;
        try {
            if(price != null)
                floatPrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>
                    (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                            "Wrong format of price field"),
                            HttpStatus.BAD_REQUEST);
        }
        attraction.setPrice(floatPrice);
        attraction.setName(name);
        attraction.setMaintenance(maintenance);
        if(image != null)
            attraction = attractionService.saveImage(attraction, image);
        if(attraction != null) {
            Boolean isEdited = attractionService.edit(attraction);
            if(!isEdited) {
                return new ResponseEntity<>
                        (new ErrorResponse(Long.parseLong(HttpStatus.NOT_FOUND.toString()),
                                "Attraction with such id was not found"),
                                HttpStatus.NOT_FOUND);
            }
        }
        else return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(HttpStatus.BAD_REQUEST.toString()),
                        "Passed empty account"),
                        HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(attractionService.get(id), HttpStatus.OK);
    }
}
