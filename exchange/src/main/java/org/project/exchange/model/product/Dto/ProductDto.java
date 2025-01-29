package org.project.exchange.model.product.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.exchange.model.product.Product;

import java.util.UUID;

import static java.lang.Integer.parseInt;


@Data
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String name;
    private Integer originPrice;
    private Integer convertedPrice;
    private String exchangeRate;

    public Product toEntity(Product product){
        return Product.builder()
                .productId(productId)
                .name(name)
                .originPrice(originPrice)
                .convertedPrice(convertedPrice)
                .exchangeRate(exchangeRate)
                .build();
    }

    public ProductDto(String originPrice, String exchangeRate) {
        this.productId = UUID.randomUUID().toString();;
        this.name = "empty";
        this.originPrice = parseInt(originPrice.toString());
        this.convertedPrice = parseInt(originPrice.toString()) * parseInt(exchangeRate.toString());
        this.exchangeRate = exchangeRate;
    }

}
