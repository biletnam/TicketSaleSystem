package ru.tersoft.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.tersoft.entity.Account;

import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    Page<Account> findAll(Pageable pageable);
}