package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.repository.AttractionRepository;
import ru.tersoft.repository.OrderRepository;
import ru.tersoft.repository.TicketRepository;
import ru.tersoft.service.OrderService;

import java.util.List;
import java.util.UUID;

@Service("OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AttractionRepository attractionRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, AttractionRepository attractionRepository, TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.attractionRepository = attractionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Iterable<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order get(UUID id) {
        return orderRepository.findOne(id);
    }

    @Override
    public Order add(Order order) {
        Order countedOrder = countTotal(order);
        countedOrder = orderRepository.saveAndFlush(countedOrder);
        setOrders(countedOrder.getId());
        return countedOrder;
    }

    @Override
    public void delete(UUID id) {
        orderRepository.delete(id);
    }

    @Override
    public void edit(Order order) {
        Order existingOrder = orderRepository.findOne(order.getId());
        if(order.isPayed() != null) existingOrder.setPayed(order.isPayed());
        if(order.getOrderdate() != null) existingOrder.setOrderdate(order.getOrderdate());
    }

    @Override
    public void addTicket(UUID orderid, Ticket ticket) {
        Order order = orderRepository.findOne(orderid);
        List<Ticket> tickets = order.getTickets();
        tickets.add(ticket);
        order.setTickets(tickets);
        order = countTotal(order);
        orderRepository.save(order);
    }

    private Order countTotal(Order order) {
        List<Ticket> tickets = order.getTickets();
        Float total = 0F;
        for (Ticket ticket : tickets) {
            Attraction currAttraction = attractionRepository.findOne(ticket.getAttraction().getId());
            total += currAttraction.getPrice();
        }
        order.setTotal(total);
        return order;
    }

    private void setOrders(UUID orderid) {
        Order order = orderRepository.findOne(orderid);
        List<Ticket> tickets = order.getTickets();
        for(int i = 0; i < tickets.size(); i++) { // Avoid ConcurrentModificationException by not using foreach
            Ticket ticket = tickets.get(i);
            ticket.setOrder(order);
            ticketRepository.saveAndFlush(ticket);
        }
    }
}
