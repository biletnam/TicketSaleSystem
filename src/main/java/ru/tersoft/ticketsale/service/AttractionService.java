package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AttractionService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> add(String name, String description, String cat, String price,
                          String maintenanceid, MultipartFile image);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(UUID id, String name, String description, String cat, String price,
                 String maintenanceid, MultipartFile image);
}
