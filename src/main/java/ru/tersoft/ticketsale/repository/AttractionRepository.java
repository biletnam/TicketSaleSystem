package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Category;
import ru.tersoft.ticketsale.entity.Maintenance;

import java.util.UUID;

public interface AttractionRepository extends JpaRepository<Attraction, UUID> {
    Iterable<Attraction> findByCategory(Category category);
    Attraction findByMaintenance(Maintenance maintenance);
}
