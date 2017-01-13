package ru.tersoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Maintenance;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.repository.MaintenanceRepository;
import ru.tersoft.service.MaintenanceService;
import ru.tersoft.utils.ResponseFactory;

import java.util.Date;
import java.util.UUID;

@Service("MaintenanceService")
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final AttractionRepository attractionRepository;

    @Autowired
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository, AttractionRepository attractionRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.attractionRepository = attractionRepository;
    }

    @Override
    public ResponseEntity<?> getAll(UUID attractionid) {
        if(attractionid != null) {
            Attraction attraction = attractionRepository.findOne(attractionid);
            if (attraction == null)
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
            else
                return ResponseFactory.createResponse(maintenanceRepository.findByAttraction(attraction));
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Attraction id was not passed");
        }
    }

    @Override
    public ResponseEntity<?> getByDate(Date today, UUID attractionid) {
        if (today != null && attractionid != null) {
            Attraction attraction = attractionRepository.findOne(attractionid);
            if (attraction != null) {
                return ResponseFactory.createResponse(maintenanceRepository.findByDate(today, attraction));
            } else {
                return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
            }
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Date or attraction id was not passed");
        }
    }

    @Override
    public Maintenance get(UUID id) {
        return maintenanceRepository.findOne(id);
    }

    @Override
    public ResponseEntity<?> add(Maintenance maintenance) {
        Attraction attraction = attractionRepository.findOne(maintenance.getAttraction().getId());
        if(attraction != null) {
            maintenance.setAttraction(attraction);
            return ResponseFactory.createResponse(maintenanceRepository.saveAndFlush(maintenance));
        }
        else return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        if(maintenanceRepository.findOne(id) == null) {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        }
        else {
            maintenanceRepository.delete(id);
            return ResponseFactory.createResponse();
        }
    }

    @Override
    public ResponseEntity<?> edit(Maintenance maintenance) {
        if(maintenance == null)
            return ResponseFactory.createErrorResponse(HttpStatus.BAD_REQUEST, "Passed empty maintenance");
        Maintenance existingMaintenance = maintenanceRepository.findOne(maintenance.getId());
        if(existingMaintenance == null)
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        if(maintenance.getStartdate() != null)
            existingMaintenance.setStartdate(maintenance.getStartdate());
        if(maintenance.getEnddate() != null)
            existingMaintenance.setEnddate(maintenance.getEnddate());
        if(maintenance.getReason() != null && !maintenance.getReason().isEmpty())
            existingMaintenance.setReason(maintenance.getReason());
        return ResponseFactory.createResponse(maintenanceRepository.saveAndFlush(existingMaintenance));
    }
}
