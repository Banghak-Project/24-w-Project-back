package org.project.exchange.model.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 구현해야 할 로직
    // 1. list별 product 보여주기
    // 2. product 생성
    // 3. product 수정
    // 4. 각 product 삭제
    // 5. list별 product 삭제
    public List<Product> findMemberByList(String listId) {
        return productRepository.findByListId(listId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(Product product) {
        productRepository.findById(product.getProductId()).ifPresent(p -> {
            p.setName(product.getName());
            p.setOriginPrice(product.getOriginPrice());
        });
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void deleteByListId(String listId) {
        productRepository.deleteByListId(listId);
    }
}
