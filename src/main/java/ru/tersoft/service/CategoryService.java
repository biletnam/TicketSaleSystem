package ru.tersoft.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.entity.Category;

import java.util.UUID;

public interface CategoryService {
    ResponseEntity<?> getAll();
    Category get(UUID id);
    ResponseEntity<?> add(Category category);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> edit(Category category);
}
