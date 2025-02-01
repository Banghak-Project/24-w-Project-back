package org.project.exchange.model.product;

import jakarta.persistence.*;
import lombok.*;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.list.Lists;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "product") // 상품 정보 테이블
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false) // 상품 ID
    private Long productId;

    @Column(name = "name", nullable = false, length = 100) // 상품 이름
    private String name;

    @Column(name = "origin_price", nullable = false) // 원래 가격
    private Double originPrice;

    @Column(name = "converted_price", nullable = false) // 변환된 가격
    private Double convertedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id") // 리스트 ID
    private Lists lists;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id") // 통화 ID
    private Currency currency;

    @Builder
    public Product(String name, Double originPrice, Double convertedPrice, Lists lists, Currency currency) {
        this.name = name;
        this.originPrice = originPrice;
        this.convertedPrice = convertedPrice;
        this.lists = lists;
        this.currency = currency;
    }
}
