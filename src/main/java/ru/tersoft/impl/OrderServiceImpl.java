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
    public Boolean delete(UUID id) {
        if(orderRepository.findOne(id) != null) {
            orderRepository.delete(id);
            return true;
        } else return false;
    }

    @Override
    public Boolean setPayed(UUID orderid) {
        Order order = orderRepository.findOne(orderid);
        if(order != null) {
            order.setPayed(true);
            orderRepository.saveAndFlush(order);
            return true;
        } else return false;
    }

    @Override
    public Boolean addTickets(UUID orderid, List<Ticket> tickets) {
        Order order = orderRepository.findOne(orderid);
        if(order != null) {
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                ticket.setAttraction(attractionRepository.findOne(ticket.getAttraction().getId()));
                ticket.setOrder(order);
                ticketRepository.saveAndFlush(ticket);
            }
            orderRepository.saveAndFlush(countTotal(order));
            return true;
        } else return false;
    }

    @Override
    public Order deleteTicket(UUID ticketid) {
        Ticket ticket = ticketRepository.findOne(ticketid);
        if(ticket != null) {
            UUID orderid = ticket.getOrder().getId();
            ticketRepository.delete(ticket);
            Order order = orderRepository.findOne(orderid);
            Order savedOrder = orderRepository.saveAndFlush(order);
            return countTotal(savedOrder);
        } else return null;
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
        for (int i = 0; i < tickets.size(); i++) { // Avoid ConcurrentModificationException by not using foreach
            Ticket ticket = tickets.get(i);
            ticket.setAttraction(attractionRepository.findOne(ticket.getAttraction().getId()));
            ticket.setOrder(order);
            ticketRepository.saveAndFlush(ticket);
        }
    }
}
