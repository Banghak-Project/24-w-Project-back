package org.project.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.project.exchange.model.product.Dto.ProductRequestDto;
import org.project.exchange.model.product.Dto.ProductResponseDto;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDto requestDto) {
        Product newProduct = productService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductResponseDto>> getProductByListsId(@PathVariable Long id) {
        List<ProductResponseDto> product = productService.findByListsId(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto requestDto) {
        Product updatedProduct = productService.update(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

