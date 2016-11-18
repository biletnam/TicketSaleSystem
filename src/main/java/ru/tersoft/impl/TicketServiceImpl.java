package ru.tersoft.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tersoft.entity.Ticket;
import ru.tersoft.repository.TicketRepository;
import ru.tersoft.service.TicketService;

import java.util.UUID;

@Service("TicketService")
@Transactional
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Iterable<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket get(UUID id) {
        return ticketRepository.findOne(id);
    }

    @Override
    public Ticket add(Ticket ticket) {
        return ticketRepository.saveAndFlush(ticket);
    }

    @Override
    public void delete(UUID id) {
        ticketRepository.delete(id);
    }
}
