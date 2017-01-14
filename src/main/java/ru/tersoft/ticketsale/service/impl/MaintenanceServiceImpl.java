package ru.tersoft.ticketsale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Maintenance;
import ru.tersoft.ticketsale.repository.AttractionRepository;
import ru.tersoft.ticketsale.repository.MaintenanceRepository;
import ru.tersoft.ticketsale.service.MaintenanceService;
import ru.tersoft.ticketsale.utils.ResponseFactory;

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
    public Maintenance get(UUID id) {
        return maintenanceRepository.findOne(id);
    }

    @Override
    public ResponseEntity<?> add(Maintenance maintenance) {
        return ResponseFactory.createResponse(maintenanceRepository.saveAndFlush(maintenance));
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        if(maintenanceRepository.findOne(id) == null) {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        } else {
            Attraction attraction = attractionRepository.findByMaintenance(maintenanceRepository.findOne(id));
            attraction.setMaintenance(null);
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
