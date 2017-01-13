package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.ticketsale.entity.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Iterable<Account> findByMail(String mail);
}