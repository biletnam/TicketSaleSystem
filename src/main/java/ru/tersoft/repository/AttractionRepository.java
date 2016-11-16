package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Attraction;

import java.util.UUID;

public interface AttractionRepository extends JpaRepository<Attraction, UUID> {
}
