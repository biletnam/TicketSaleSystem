package ru.tersoft.ticketsale.service;

import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    ResponseEntity<?> get(UUID id, String mail);
    Order getCart(Account account);
    ResponseEntity<?> getByAccount(Account account);
    ResponseEntity<?> finishOrder(Account account, Date visitdate);
    ResponseEntity<?> addTickets(Account account, List<String> attractions);
    ResponseEntity<?> disableTicket(UUID ticketid);
    ResponseEntity<?> deleteTickets(Account account, List<String> attractions);
}
