package ru.tersoft.service;

import ru.tersoft.entity.Ticket;

import java.util.UUID;

public interface TicketService {
    Iterable<Ticket> getAll();
    Ticket get(UUID id);
    Ticket add(Ticket ticket);
    void delete(UUID id);
}
