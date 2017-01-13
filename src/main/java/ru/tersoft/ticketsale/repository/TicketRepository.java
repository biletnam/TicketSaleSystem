package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.ticketsale.entity.Ticket;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
