package ru.tersoft.ticketsale.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Category;
import ru.tersoft.ticketsale.entity.Maintenance;
import ru.tersoft.ticketsale.repository.AttractionRepository;
import ru.tersoft.ticketsale.repository.CategoryRepository;
import ru.tersoft.ticketsale.repository.MaintenanceRepository;
import ru.tersoft.ticketsale.repository.TicketRepository;
import ru.tersoft.ticketsale.service.AttractionService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service("AttractionService")
@Transactional(rollbackFor=LockAcquisitionException.class)
public class AttractionServiceImpl implements AttractionService {
    private final AttractionRepository attractionRepository;
    private final CategoryRepository categoryRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final TicketRepository ticketRepository;

    @Value("${ticketsale.images-folder}")
    private String imagesLocation;

    @Value("${ticketsale.attraction-height}")
    private int imagesHeight;
    @Value("${ticketsale.attraction-width}")
    private int imagesWidth;

    @Autowired
    public AttractionServiceImpl(AttractionRepository attractionRepository, CategoryRepository categoryRepository, MaintenanceRepository maintenanceRepository, TicketRepository ticketRepository) {
        this.attractionRepository = attractionRepository;
        this.categoryRepository = categoryRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.ticketRepository = ticketRepository;
    }

    public ResponseEntity<?> getAll() {
        return ResponseFactory.createResponse(attractionRepository.findAll());
    }

    public ResponseEntity<?> get(UUID id) {
        Attraction attraction = attractionRepository.findOne(id);;
        if(attraction == null)
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        else
            return ResponseFactory.createResponse(attraction);
    }

    public ResponseEntity<?> add(String name, String description, String cat, String price,
                                 String maintenanceid, MultipartFile image) {
        Attraction attraction = new Attraction();
        if(cat != null) {
            Category category = categoryRepository.findOne((UUID.fromString(cat)));
            if (category == null)
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Category with such id was not found");
            attraction.setCategory(category);
        }
        attraction.setDescription(description);
        BigDecimal decimalPrice = null;
        try {
            if(price != null)
                decimalPrice = new BigDecimal(price);
            else ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Empty price field");
        } catch (NumberFormatException e) {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong format of price field");
        }
        attraction.setPrice(decimalPrice);
        attraction.setName(name);
        if(maintenanceid != null) {
            Maintenance maintenance = maintenanceRepository.findOne(UUID.fromString(maintenanceid));
            if(maintenance != null)
                attraction.setMaintenance(maintenance);
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        }
        if(image != null && !image.isEmpty()) {
            try {
                attraction = saveImage(attraction, image);
                if(attraction == null)
                    return ResponseFactory.createErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "Unsupported image content type");
            } catch (IOException e) {
                return ResponseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Image was not selected");
        }
        return ResponseFactory.createResponse(attractionRepository.saveAndFlush(attraction));
    }

    public ResponseEntity<?> delete(UUID id) {
        if(attractionRepository.findOne(id) == null) {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        } else {
            ticketRepository.deleteByAttraction(attractionRepository.findOne(id));
            deleteImage(id);
            attractionRepository.delete(id);
            return ResponseFactory.createResponse();
        }
    }

    public ResponseEntity<?> edit(UUID id, String name, String description, String cat, String price,
                        String maintenanceid, MultipartFile image) {
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
        BigDecimal decimalPrice = null;
        try {
            if(price != null)
                decimalPrice = new BigDecimal(price);
        } catch (NumberFormatException e) {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Wrong format of price field");
        }
        if(name != null && !name.isEmpty())
            existingAttraction.setName(name);
        if(decimalPrice != null)
            existingAttraction.setPrice(decimalPrice);
        if(description != null && !description.isEmpty())
            existingAttraction.setDescription(description);
        if(category != null)
            existingAttraction.setCategory(category);
        if(maintenanceid != null) {
            Maintenance maintenance = maintenanceRepository.findOne(UUID.fromString(maintenanceid));
            if(maintenance != null)
                existingAttraction.setMaintenance(maintenance);
            else
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND,
                        "Maintenance with such id was not found");
        }
        if(image != null && !image.isEmpty()) {
            deleteImage(existingAttraction.getId());
            try {
                existingAttraction = saveImage(existingAttraction, image);
                if(existingAttraction == null)
                    return ResponseFactory.createErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "Unsupported image content type");
            } catch (IOException e) {
                return ResponseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return ResponseFactory.createResponse(attractionRepository.saveAndFlush(existingAttraction));
    }

    private Attraction saveImage(Attraction attraction, MultipartFile image) throws IOException {
        if(image.getContentType().equals("image/png")) {
            attraction = attractionRepository.saveAndFlush(attraction);
            String filename = imagesLocation + "attractions/" + attraction.getId() + ".png";
            File file = new File(filename);
            image.transferTo(file);
            Thumbnails.of(file)
                    .forceSize(imagesWidth, imagesHeight)
                    .outputFormat("png")
                    .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
            attraction.setImage("/img/attractions/" + attraction.getId() + ".png");
            attraction.setThumbnail("/img/attractions/thumbnail."
                    + attraction.getId() + ".png");
            return attraction;
        } else return null;
    }

    private Boolean deleteImage(UUID attractionid) {
        String id = attractionid.toString();
        String filePath = imagesLocation + "attractions/" + id + ".png";
        File myFile = new File(filePath);
        if(myFile.exists()) {
            boolean ans = myFile.delete();
            filePath = imagesLocation + "attractions/thumbnail." + id + ".png";
            myFile = new File(filePath);
            return ans && myFile.delete();
        } else {
            return null;
        }
    }
}
