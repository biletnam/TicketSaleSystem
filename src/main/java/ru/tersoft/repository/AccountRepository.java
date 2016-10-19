package ru.tersoft.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tersoft.entity.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {}