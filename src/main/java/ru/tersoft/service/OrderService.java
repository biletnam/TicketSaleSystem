package ru.tersoft.service;

import org.springframework.data.domain.Page;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Page<Order> getAll(int page, int limit);
    Order get(UUID id);
    Iterable<Order> getByAccount(UUID accountid);
    Order add(Order order);
    void delete(UUID id);
    void setPayed(UUID orderid);
    void addTickets(UUID orderid, List<Ticket> tickets);
    void deleteTicket(UUID ticketid);
}
