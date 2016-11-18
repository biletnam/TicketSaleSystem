package ru.tersoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.entity.Account;
import ru.tersoft.entity.Dialog;

import java.util.UUID;

public interface DialogRepository extends JpaRepository<Dialog, UUID> {
    Iterable<Dialog> findByUser(Account account);
}
