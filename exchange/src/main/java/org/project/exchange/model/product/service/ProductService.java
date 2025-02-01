package org.project.exchange.model.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.currency.repository.CurrencyRepository;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.list.repository.ListsRepository;
import org.project.exchange.model.product.Dto.ProductRequestDto;
import org.project.exchange.model.product.Dto.ProductResponseDto;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ListsRepository listsRepository;
    private final CurrencyRepository currencyRepository;

    // 구현해야 할 로직
    // 1. list별 product 보여주기
    // 2. product 생성
    // 3. product 수정
    // 4. 각 product 삭제
    // 5. list별 product 삭제
    public List<ProductResponseDto> findByListId(Long listId) {
        return productRepository.findByListId(listId)
                .stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }
    public void save(ProductRequestDto requestDto) {
        Lists lists = listsRepository.findById(requestDto.getListId())
                .orElseThrow(() -> new IllegalArgumentException("해당 리스트가 존재하지 않습니다."));
        Currency currency = currencyRepository.findById(requestDto.getCurrencyId())
                .orElseThrow(() -> new IllegalArgumentException("해당 통화가 존재하지 않습니다."));

        Product product = requestDto.toEntity(lists, currency);
        productRepository.save(product);
    }
    public void update(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        product.setName(requestDto.getName());
        product.setOriginPrice(requestDto.getOriginPrice());
        product.setConvertedPrice(requestDto.getConvertedPrice());
    }


    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        productRepository.delete(product);
    }

    public void deleteByListId(Long listId) {
        productRepository.deleteByListId(listId);
    }
}
