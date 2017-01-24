package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tersoft.ticketsale.entity.Account;
import ru.tersoft.ticketsale.entity.Order;

import java.util.Date;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("select o from Order o where o.visitdate = ?1 and o.payed = true")
    Iterable<Order> findExpiredOrders(Date visitDate);

    @Query("select o from Order o where o.account = ?1 and o.orderdate >= ?2 and o.payed = true")
    Iterable<Order> findOrders(Account account, Date minDate);

    @Query("select o from Order o where o.account = ?1 and o.payed = false")
    Order findCart(Account account);
}
