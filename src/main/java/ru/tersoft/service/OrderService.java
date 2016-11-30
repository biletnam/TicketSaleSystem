package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Page<Order> getAll(int page, int limit);
    Order get(UUID id);
    Iterable<Order> getByAccount(UUID orderid);
    Order add(Order order);
    Boolean delete(UUID id);
    Boolean setPayed(UUID orderid);
    Boolean addTickets(UUID orderid, List<Ticket> tickets);
    Boolean disableTicket(UUID ticketid);
    Order deleteTicket(UUID ticketid);
}
