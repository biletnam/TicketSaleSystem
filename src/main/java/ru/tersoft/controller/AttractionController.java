package ru.tersoft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.entity.Attraction;
import ru.tersoft.service.AttractionService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("attractions")
@Api(description = "Work with attractions", tags = {"Attraction"})
public class AttractionController {
    @Resource(name="AttractionService")
    private AttractionService attractionService;
    private String imagesLocation = "C:/Users/termi/IdeaProjects/ticketsale/src/main/resources/images/attractions/";

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Get list of attractions")
    public List<Attraction> getAttractions() {
        return (List<Attraction>)attractionService.getAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "Add new attraction", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    public ResponseEntity<?> add(@RequestPart(value = "name") String name,
                                 @RequestPart(value = "description") String description,
                                 @RequestPart(value = "maintenance", required = false) Boolean maintenance,
                                 @RequestPart(value = "image", required = false) MultipartFile image) {
        Attraction attraction = new Attraction();
        attraction.setDescription(description);
        attraction.setName(name);
        attraction.setMaintenance(maintenance);
        if(image != null)
            attraction = saveImage(attraction, image);
        if(attraction != null)
            attractionService.add(attraction);
        else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    Attraction saveImage(Attraction attraction, MultipartFile image) {
        try {
            if (!image.isEmpty()) {
                if(image.getContentType().equals("image/jpeg")) {
                    UUID imageId = UUID.randomUUID();
                    String filename = imagesLocation + imageId + ".jpg";
                    File file = new File(filename);
                    image.transferTo(file);
                    Thumbnails.of(file)
                            .size(200, 150)
                            .outputFormat("jpg")
                            .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
                    attraction.setImagepath("/images/attractions/" + imageId + ".jpg");
                    attraction.setSmallimagepath("/images/attractions/thumbnail."
                            + imageId + ".jpg");
                    return attraction;
                }
            }
        }
        catch(IOException e) {
            Logger log = Logger.getLogger(AttractionController.class);
            log.error(e);
            //TODO: Exception handler
        }
        return null;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiOperation(value = "Delete attraction", notes = "Admin access required")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        attractionService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get attraction by id")
    public Attraction get(@PathVariable("id") UUID id) {
        Attraction attraction = attractionService.get(id);
        return attraction;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "Access token", required = true, dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "Edit attraction info", notes = "Admin access required")
    public ResponseEntity<Attraction> edit(@PathVariable("id") UUID id,
                                           @RequestPart(value = "name", required = false) String name,
                                           @RequestPart(value = "description", required = false) String description,
                                           @RequestPart(value = "maintaince", required = false) Boolean maintaince,
                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        Attraction attraction = new Attraction();
        attraction.setId(id);
        attraction.setDescription(description);
        attraction.setName(name);
        attraction.setMaintenance(maintaince);
        if(image != null)
            attraction = saveImage(attraction, image);
        if(attraction != null)
            attractionService.edit(attraction);
        else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(attractionService.get(id), HttpStatus.OK);
    }
}
