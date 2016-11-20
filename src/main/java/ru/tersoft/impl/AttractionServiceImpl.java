package ru.tersoft.impl;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.controller.AttractionController;
import ru.tersoft.entity.Attraction;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.service.AttractionService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("AttractionService")
@Transactional
public class AttractionServiceImpl implements AttractionService {
    private final AttractionRepository attractionRepository;

    @Value("${ticketsale.images-folder}")
    private String imagesLocation;

    @Value("${ticketsale.attraction-height}")
    private int imagesHeight;
    @Value("${ticketsale.attraction-width}")
    private int imagesWidth;

    @Autowired
    public AttractionServiceImpl(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    public Iterable<Attraction> getAll() {
        return attractionRepository.findAll();
    }

    public Attraction get(UUID id) {
        return attractionRepository.findOne(id);
    }

    public Attraction add(Attraction attraction) {
        if(attraction.getMaintenance() == null) attraction.setMaintenance(true);
        return attractionRepository.saveAndFlush(attraction);
    }

    public Boolean delete(UUID id) {
        if(attractionRepository.findOne(id) == null) return false;
        else {
            attractionRepository.delete(id);
            return true;
        }
    }

    public Boolean edit(Attraction attraction) {
        if(attraction == null) return false;
        if(attraction.getId() == null) return false;
        Attraction existingAttraction = attractionRepository.findOne(attraction.getId());
        if(existingAttraction == null) return false;
        if(attraction.getName() != null&& !attraction.getName().isEmpty())
            existingAttraction.setName(attraction.getName());
        if(attraction.getPrice() != null)
            existingAttraction.setPrice(attraction.getPrice());
        if(attraction.getDescription() != null && !attraction.getDescription().isEmpty())
            existingAttraction.setDescription(attraction.getDescription());
        if(attraction.getImagepath() != null)
            existingAttraction.setImagepath(attraction.getImagepath());
        if(attraction.getSmallimagepath() != null)
            existingAttraction.setSmallimagepath(attraction.getSmallimagepath());
        if(attraction.getMaintenance() != null)
            existingAttraction.setMaintenance(attraction.getMaintenance());
        attractionRepository.save(existingAttraction);
        return true;
    }

    public Attraction saveImage(Attraction attraction, MultipartFile image) {
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
}
