package org.project.exchange.model.product;

import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.photo.Photo;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "상품정보")
public class Product {

    @Id
    @Column(name = "상품정보_id", length = 255)
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "리스트_id", nullable = false)
    private Lists list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "통화_id", nullable = false)
    private Currency currency;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "사진_id", nullable = false)
    private Photo photo;

    @Column(name = "상품정보_name", length = 255)
    private String name;

    @Column(name = "상품정보_origin_price", length = 255)
    private Integer originPrice;

    @Column(name = "상품정보_converted_price", length = 255)
    private Integer convertedPrice;

    @Column(name = "상품정보_exchange_rate", length = 255)
    private String exchangeRate;
}
