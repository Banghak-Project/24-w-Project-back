package org.project.exchange.model.list.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.model.list.Lists;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Slf4j
public class CreateListWithNameRequestDto {
    private Long userId;  // 사용자 ID
    private String name; // 리스트 이름
    private Long currencyIdFrom; // 통화 ID
    private Long currencyIdTo; // 통화 ID
    private LocalDateTime now;
    private String location; // 위치

    public CreateListWithNameRequestDto(Lists lists) {
        this.userId = lists.getUser().getUserId();
        this.currencyIdFrom = lists.getCurrencyFrom().getCurrencyId();
        this.currencyIdTo = lists.getCurrencyTo().getCurrencyId();
        this.now = lists.getCreatedAt();
        this.location = lists.getLocation();
        this.name = lists.getName();
    }
}
