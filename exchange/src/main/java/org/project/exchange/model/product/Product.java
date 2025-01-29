package org.project.exchange.model.product;

import jakarta.persistence.*;
import lombok.*;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.photo.Photo;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
<<<<<<< Updated upstream
@Table(name = "product") // 상품 정보 테이블
=======
@Getter
@Table(name = "상품정보")
>>>>>>> Stashed changes
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
    private Lists list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id") // 통화 ID
    private Currency currency;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id") // 사진 ID
    private Photo photo;

<<<<<<< Updated upstream
    @Builder
    public Product(Long productId, String name, Double originPrice, Double convertedPrice, Lists list, Currency currency,
            Photo photo) {
        this.productId = productId;
        this.name = name;
        this.originPrice = originPrice;
        this.convertedPrice = convertedPrice;
        this.list = list;
        this.currency = currency;
        this.photo = photo;
    }
=======
    @Column(name = "상품정보_name", length = 255)
    private String name;

    @Column(name = "상품정보_origin_price", length = 255)
    private Integer originPrice;

    @Column(name = "상품정보_converted_price", length = 255)
    private Integer convertedPrice;

    @Column(name = "상품정보_exchange_rate", length = 255)
    private String exchangeRate;
>>>>>>> Stashed changes
}
