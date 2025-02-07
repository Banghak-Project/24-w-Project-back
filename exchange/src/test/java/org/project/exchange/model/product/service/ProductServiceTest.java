package org.project.exchange.model.product.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.list.repository.ListsRepository;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.repository.ProductRepository;
import org.project.exchange.model.user.User;
import org.project.exchange.model.user.repository.UserRepository;
import org.project.exchange.model.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ListsRepository listsRepository;

    @Autowired
    EntityManager em;

    @Test
    void findMemberByList() {
    }

    @Test
    @Rollback(false)
    void save() throws ParseException {
        //given
//        User testuser = userRepository.findByUserUsername("jieun");
//        Lists testlist = new Lists("List1", null, testuser);
//        listsRepository.save(testlist);
        Lists myList = listsRepository.findById(Long.valueOf("1"))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 리스트를 찾을 수 없습니다."));
        Product product = new Product("name", 1000.0, 1000.0, myList, null);
//        productService.save(product);

        //when

        //then
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


