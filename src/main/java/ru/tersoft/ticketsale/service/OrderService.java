package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.Order;
import ru.tersoft.ticketsale.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    ResponseEntity<?> getAll(int page, int limit);
    ResponseEntity<?> get(UUID id);
    ResponseEntity<?> getByAccount(UUID orderid);
    ResponseEntity<?> add(Order order);
    ResponseEntity<?> delete(UUID id);
    ResponseEntity<?> setPayed(UUID orderid);
    ResponseEntity<?> addTickets(UUID orderid, List<Ticket> tickets);
    ResponseEntity<?> disableTicket(UUID ticketid);
    ResponseEntity<?> deleteTicket(UUID ticketid);
}
