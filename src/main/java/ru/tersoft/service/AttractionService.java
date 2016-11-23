package ru.tersoft.service;

import org.springframework.web.multipart.MultipartFile;
import ru.tersoft.entity.Attraction;

import java.util.UUID;

public interface AttractionService {
    Iterable<Attraction> getAll();
    Iterable<Attraction> getByCategory(UUID id);
    Attraction get(UUID id);
    Attraction add(Attraction account);
    Boolean delete(UUID id);
    Boolean edit(Attraction account);
    Attraction saveImage(Attraction attraction, MultipartFile image);
}
