package ru.tersoft.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tersoft.entity.Account;

import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {}