package org.project.exchange.model.list.Dto;

import lombok.Getter;
import org.project.exchange.model.list.Lists;

@Getter
public class ListsResponseDto {
    private Long listId;
    private String name;
    private Long userId;

    public ListsResponseDto(Lists lists) {
        this.listId = lists.getListId();
        this.name = lists.getName();
        this.userId = lists.getUser().getUserId();
    }
}
