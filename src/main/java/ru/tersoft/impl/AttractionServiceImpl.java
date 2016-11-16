package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.service.AttractionService;

import java.util.UUID;

@Service("AttractionService")
@Transactional
public class AttractionServiceImpl implements AttractionService {
    private final AttractionRepository attractionRepository;

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

    public void delete(UUID id) {
        attractionRepository.delete(id);
    }

    public void edit(Attraction attraction) {
        Attraction existingAttraction = attractionRepository.findOne(attraction.getId());
        if(attraction.getName() != null)
            existingAttraction.setName(attraction.getName());
        if(attraction.getDescription() != null)
            existingAttraction.setDescription(attraction.getDescription());
        if(attraction.getImagepath() != null)
            existingAttraction.setImagepath(attraction.getImagepath());
        if(attraction.getSmallimagepath() != null)
            existingAttraction.setSmallimagepath(attraction.getSmallimagepath());
        if(attraction.getMaintenance() != null)
            existingAttraction.setMaintenance(attraction.getMaintenance());
        attractionRepository.save(existingAttraction);
    }
}
