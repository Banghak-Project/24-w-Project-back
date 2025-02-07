package org.project.exchange.model.product.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.product.Product;

@Getter
@NoArgsConstructor
public class ProductRequestDto {
    private String name;
    private Double originPrice;
    private Double convertedPrice;
    private Long listId;
    private Long currencyId;

    public Product toEntity(Lists lists, Currency currency) {
        return Product.builder()
                .name(this.name)
                .originPrice(this.originPrice)
                .convertedPrice(this.convertedPrice)
                .lists(lists)
                .currency(currency)
                .build();
    }
}
