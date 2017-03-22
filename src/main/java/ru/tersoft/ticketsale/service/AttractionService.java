package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.ticketsale.entity.Maintenance;

import java.util.UUID;

public interface AttractionService {
    void markTickets(UUID attractionid, Maintenance maintenance, boolean broken);
    ResponseEntity<?> getAll();
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> add(String name, String description, String cat, String price,
                          String maintenanceid, MultipartFile image);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(UUID id, String name, String description, String cat, String price,
                 String maintenanceid, MultipartFile image);
}
