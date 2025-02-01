package org.project.exchange.model.product.Dto;

import lombok.Getter;
import org.project.exchange.model.product.Product;

@Getter
public class ProductResponseDto {
    private Long productId;
    private String name;
    private Double originPrice;
    private Double convertedPrice;
    private Long listId;
    private Long currencyId;

    public ProductResponseDto(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.originPrice = product.getOriginPrice();
        this.convertedPrice = product.getConvertedPrice();
        this.listId = product.getLists().getListId();
        this.currencyId = product.getCurrency().getCurrencyId();
    }
}
