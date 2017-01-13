package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.Maintenance;

import java.util.Date;
import java.util.UUID;

public interface MaintenanceService {
    ResponseEntity<?> getByDate(Date today, UUID attractionid);
    Maintenance get(UUID id);
    ResponseEntity<?> add(Maintenance maintenance);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(Maintenance maintenance);
}
