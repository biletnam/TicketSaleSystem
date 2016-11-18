package ru.tersoft.service;

import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;

import java.util.UUID;

public interface OrderService {
    Iterable<Order> getAll();
    Order get(UUID id);
    Order add(Order order);
    void delete(UUID id);
    void edit(Order order);
    void addTicket(UUID orderid, Ticket ticket);
}
