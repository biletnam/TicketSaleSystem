package ru.tersoft.impl;

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

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private AttractionRepository attractionRepository;
    @Override
    public Iterable<Maintenance> getAll(UUID attractionid) {
        Attraction attraction = attractionRepository.findOne(attractionid);
        return maintenanceRepository.findByAttraction(attraction);
    }

    @Override
    public Iterable<Maintenance> getAll(Date today) {
        return maintenanceRepository.findByDate(today);
    }

    @Override
    public Maintenance get(UUID id) {
        return maintenanceRepository.findOne(id);
    }

    @Override
    public Maintenance add(Maintenance maintenance) {
        return maintenanceRepository.saveAndFlush(maintenance);
    }

    @Override
    public void delete(UUID id) {
        maintenanceRepository.delete(id);
    }

    @Override
    public void edit(Maintenance maintenance) {
        Maintenance existingMaintenance = maintenanceRepository.findOne(maintenance.getId());
        if(maintenance.getStartdate() != null)
            existingMaintenance.setStartdate(maintenance.getStartdate());
        if(maintenance.getEnddate() != null)
            existingMaintenance.setEnddate(maintenance.getEnddate());
        if(maintenance.getReason() != null)
            existingMaintenance.setReason(maintenance.getReason());
        maintenanceRepository.save(existingMaintenance);
    }
}
