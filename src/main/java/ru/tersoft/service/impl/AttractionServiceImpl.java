package ru.tersoft.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.controller.AttractionController;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Category;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.repository.CategoryRepository;
import ru.tersoft.service.AttractionService;
import ru.tersoft.utils.ResponseFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("AttractionService")
@Transactional
public class AttractionServiceImpl implements AttractionService {
    private final AttractionRepository attractionRepository;
    private final CategoryRepository categoryRepository;

    @Value("${ticketsale.images-folder}")
    private String imagesLocation;

    @Value("${ticketsale.attraction-height}")
    private int imagesHeight;
    @Value("${ticketsale.attraction-width}")
    private int imagesWidth;

    @Autowired
    public AttractionServiceImpl(AttractionRepository attractionRepository, CategoryRepository categoryRepository) {
        this.attractionRepository = attractionRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<?> getAll() {
        return ResponseFactory.createResponse(attractionRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getByCategory(UUID id) {
        if(id != null) {
            Category category = categoryRepository.findOne(id);
            if(category == null)
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
            else
                return ResponseFactory.createResponse(category);
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Category id was not passed");
        }
    }
    public ResponseEntity<?> get(UUID id) {
        Attraction attraction = attractionRepository.findOne(id);;
        if(attraction == null)
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        else
            return ResponseFactory.createResponse(attraction);
    }

    public ResponseEntity<?> add(String name, String description, String cat, String price,
                                 Boolean maintenance, MultipartFile image) {
        Attraction attraction = new Attraction();
        if(cat != null) {
            Category category = categoryRepository.findOne((UUID.fromString(cat)));
            if (category == null)
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
            attraction.setCategory(category);
        }
        attraction.setDescription(description);
        Float floatPrice = null;
        try {
            if(price != null)
                floatPrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong format of price field");
        }
        attraction.setPrice(floatPrice);
        attraction.setName(name);
        if(maintenance != null)
            attraction.setMaintenance(maintenance);
        else attraction.setMaintenance(true);
        if(image != null)
            attraction = saveImage(attraction, image);
        else
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Image was not selected");
        return ResponseFactory.createResponse(attractionRepository.saveAndFlush(attraction));
    }

    public ResponseEntity<?> delete(UUID id) {
        if(attractionRepository.findOne(id) == null) {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        } else {
            attractionRepository.delete(id);
            return ResponseFactory.createResponse();
        }
    }

    public ResponseEntity<?> edit(UUID id, String name, String description, String cat, String price,
                        Boolean maintenance, MultipartFile image) {
        if(id == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Attraction with empty id");
        Attraction existingAttraction = attractionRepository.findOne(id);
        if(existingAttraction == null)
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        Category category = null;
        if(cat != null) {
            category = categoryRepository.findOne(UUID.fromString(cat));
            if (category == null)
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
        }
        Float floatPrice = null;
        try {
            if(price != null)
                floatPrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong format of price field");
        }
        if(name != null && !name.isEmpty())
            existingAttraction.setName(name);
        if(floatPrice != null)
            existingAttraction.setPrice(floatPrice);
        if(description != null && !description.isEmpty())
            existingAttraction.setDescription(description);
        if(category != null)
            existingAttraction.setCategory(category);
        if(maintenance != null)
            existingAttraction.setMaintenance(maintenance);
        if(image != null)
            existingAttraction = saveImage(existingAttraction, image);
        return ResponseFactory.createResponse(attractionRepository.saveAndFlush(existingAttraction));
    }

    private Attraction saveImage(Attraction attraction, MultipartFile image) {
        try {
            if (!image.isEmpty()) {
                if(image.getContentType().equals("image/jpeg")) {
                    UUID imageId = UUID.randomUUID();
                    String filename = imagesLocation + "attractions/" + imageId + ".jpg";
                    File file = new File(filename);
                    image.transferTo(file);
                    Thumbnails.of(file)
                            .size(imagesWidth, imagesHeight)
                            .outputFormat("jpg")
                            .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
                    attraction.setImagepath("/img/attractions/" + imageId + ".jpg");
                    attraction.setSmallimagepath("/img/attractions/thumbnail."
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
}