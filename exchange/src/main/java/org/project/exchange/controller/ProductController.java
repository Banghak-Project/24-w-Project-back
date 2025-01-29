package org.project.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.repository.ProductRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Product")
public class ProductController {
    private final ProductRepository productRepository;

//    @PostMapping("/add")
//    public String addProduct(Product product){
//        Product savedProduct = productRepository.save(product);
//        return savedProduct.getOriginPrice();
//    }

}
