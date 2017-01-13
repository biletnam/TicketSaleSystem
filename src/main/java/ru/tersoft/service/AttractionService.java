package ru.tersoft.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AttractionService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> getByCategory(UUID id);
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> add(String name, String description, String cat, String price,
                          Boolean maintenance, MultipartFile image);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(UUID id, String name, String description, String cat, String price,
                 Boolean maintenance, MultipartFile image);
}
