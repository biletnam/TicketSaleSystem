package ru.tersoft.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.entity.Maintenance;

import java.util.Date;
import java.util.UUID;

public interface MaintenanceService {
    ResponseEntity<?> getAll(UUID attractionid);
    ResponseEntity<?> getByDate(Date today, UUID attractionid);
    Maintenance get(UUID id);
    ResponseEntity<?> add(Maintenance maintenance);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(Maintenance maintenance);
}
