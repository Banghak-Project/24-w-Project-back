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
    private Double originPrice;
    private Double convertedPrice;
    private String listId;
    private String currencyId;

    public Product toEntity(){
        return Product.builder()
                .productId(productId)
                .name(name)
                .originPrice(Double.valueOf(originPrice))
                .convertedPrice(Double.valueOf(convertedPrice))
                .build();
    }

    public ProductDto(Double originPrice, String listId, String currencyId) {
        this.productId = UUID.randomUUID().toString();
        this.name = "empty";
        this.originPrice = Double.valueOf(originPrice);
        this.convertedPrice = originPrice * 1; // 1대신 currency 불러와서 넣어야함
        this.listId = listId;
        this.currencyId = currencyId;
    }
}
