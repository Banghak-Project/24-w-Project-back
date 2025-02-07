package org.project.exchange.model.list.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.project.exchange.model.list.Lists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListsRepository extends JpaRepository<Lists, Long> {
}


