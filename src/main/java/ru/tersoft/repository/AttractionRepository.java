package ru.tersoft.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tersoft.entity.Attraction;

import java.util.UUID;

public interface AttractionRepository extends CrudRepository<Attraction, UUID> {
}
