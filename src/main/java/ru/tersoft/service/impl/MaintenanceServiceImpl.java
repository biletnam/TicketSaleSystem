package ru.tersoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Maintenance;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.repository.MaintenanceRepository;
import ru.tersoft.service.MaintenanceService;

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
    public Iterable<Maintenance> getAll(UUID attractionid) {
        Attraction attraction = attractionRepository.findOne(attractionid);
        return maintenanceRepository.findByAttraction(attraction);
    }

    @Override
    public Iterable<Maintenance> getByDate(Date today, UUID attractionid) {
        Attraction attraction = attractionRepository.findOne(attractionid);
        if(attraction != null) {
            return maintenanceRepository.findByDate(today, attraction);
        } else return null;
    }

    @Override
    public Maintenance get(UUID id) {
        return maintenanceRepository.findOne(id);
    }

    @Override
    public Maintenance add(Maintenance maintenance) {
        Attraction attraction = attractionRepository.findOne(maintenance.getAttraction().getId());
        if(attraction != null) {
            maintenance.setAttraction(attraction);
            return maintenanceRepository.saveAndFlush(maintenance);
        }
        else return null;
    }

    @Override
    public Boolean delete(UUID id) {
        if(maintenanceRepository.findOne(id) == null) return false;
        else {
            maintenanceRepository.delete(id);
            return true;
        }
    }

    @Override
    public Boolean edit(Maintenance maintenance) {
        if(maintenance == null) return false;
        Maintenance existingMaintenance = maintenanceRepository.findOne(maintenance.getId());
        if(existingMaintenance == null) return false;
        if(maintenance.getStartdate() != null)
            existingMaintenance.setStartdate(maintenance.getStartdate());
        if(maintenance.getEnddate() != null)
            existingMaintenance.setEnddate(maintenance.getEnddate());
        if(maintenance.getReason() != null && !maintenance.getReason().isEmpty())
            existingMaintenance.setReason(maintenance.getReason());
        maintenanceRepository.save(existingMaintenance);
        return true;
    }
}
