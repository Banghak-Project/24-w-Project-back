package org.project.exchange.model.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.project.exchange.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

}
