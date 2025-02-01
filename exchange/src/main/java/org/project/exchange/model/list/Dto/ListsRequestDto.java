package org.project.exchange.model.list.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.user.User;

@Getter
@NoArgsConstructor
public class ListsRequestDto {
    private String name;
    private Long userId;  // 사용자 ID

    public Lists toEntity(User user) {
        return Lists.builder()
                .name(this.name)
                .user(user)
                .build();
    }
}
