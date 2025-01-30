package org.project.exchange.model.product.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    EntityManager em;

    @Test
    void findMemberByList() {
    }

    @Test
    @Rollback(false)
    void save() {
        //given
        Product product = new Product("1", "name", 1000.0, 1000.0, null, null);

        //when
        productService.save(product);

        //then
        em.flush();
        assertEquals(product, productRepository.findById(product.getProductId()));
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteByListId() {
    }
}


