package ru.tersoft.service;

import ru.tersoft.entity.Attraction;

import java.util.UUID;

public interface AttractionService {
    Iterable<Attraction> getAll();
    Attraction get(UUID id);
    Attraction add(Attraction account);
    void delete(UUID id);
    void edit(Attraction account);
}
