package ru.tersoft.ticketsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tersoft.ticketsale.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
