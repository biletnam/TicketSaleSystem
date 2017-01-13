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

import java.util.Date;
import java.util.List;
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
    public ResponseEntity<?> getByDate(Date today, UUID attractionid) {
        if (today != null && attractionid != null) {
            Attraction attraction = attractionRepository.findOne(attractionid);
            if (attraction != null) {
                List<Maintenance> maintenanceList = (List<Maintenance>) maintenanceRepository.findByDate(today, attraction);
                if(maintenanceList.size() == 0)
                    return ResponseFactory.createResponse();
                else
                    return ResponseFactory.createResponse(maintenanceList.get(0));
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
        } else {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Attraction with such id was not found");
        }
    }

    @Override
    public ResponseEntity<?> delete(UUID id) {
        if(maintenanceRepository.findOne(id) == null) {
            return ResponseFactory.createErrorResponse(HttpStatus.NOT_FOUND, "Maintenance with such id was not found");
        } else {
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
