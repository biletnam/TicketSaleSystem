package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Attraction;
import ru.tersoft.entity.Order;
import ru.tersoft.entity.Ticket;
import ru.tersoft.repository.AccountRepository;
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
    private final AccountRepository accountRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, AttractionRepository attractionRepository, TicketRepository ticketRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.attractionRepository = attractionRepository;
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Order> getAll(int pagenum, int limit) {
        return orderRepository.findAll(new PageRequest(pagenum, limit));
    }

    @Override
    public Order get(UUID id) {
        return orderRepository.findOne(id);
    }

    @Override
    public Iterable<Order> getByAccount(UUID accountid) {
        return orderRepository.findByAccount(accountRepository.findOne(accountid));
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
    public void setPayed(UUID orderid) {
        Order order = orderRepository.findOne(orderid);
        order.setPayed(true);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void addTickets(UUID orderid, List<Ticket> tickets) {
        Order order = orderRepository.findOne(orderid);
        List<Ticket> savedTickets = order.getTickets();
        for(int i = 0; i < tickets.size(); i++) {
            Ticket savedTicket = ticketRepository.saveAndFlush(tickets.get(i));
            savedTickets.add(savedTicket);
        }
        order.setTickets(savedTickets);
        order = countTotal(order);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void deleteTicket(UUID ticketid) {
        Ticket ticket = ticketRepository.findOne(ticketid);
        Order order = ticket.getOrder();
        List<Ticket> tickets = order.getTickets();
        tickets.remove(ticket);
        order.setTickets(tickets);
        orderRepository.saveAndFlush(order);
        ticket.setOrder(null);
        ticketRepository.delete(ticketid);
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
