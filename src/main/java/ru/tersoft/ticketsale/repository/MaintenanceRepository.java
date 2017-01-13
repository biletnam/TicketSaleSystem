package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tersoft.ticketsale.entity.Attraction;
import ru.tersoft.ticketsale.entity.Maintenance;

import java.util.Date;
import java.util.UUID;

public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    Iterable<Maintenance> findByAttraction(Attraction attraction);

    @Query("select m from Maintenance m where m.startdate <= ?1 and (m.enddate >= ?1 or m.enddate = NULL) and m.attraction = ?2")
    Iterable<Maintenance> findByDate(Date today, Attraction attraction);
}
