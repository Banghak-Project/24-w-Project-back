package org.project.exchange.model.list.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.user.User;

@Getter
@NoArgsConstructor
@Slf4j
public class ListsRequestDto {
    private String name;
    private Long userId;  // 사용자 ID

    public Lists toEntity(User user) {
        log.info("ListsRequestDto toEntity() called");
        log.info("name : " + this.name);
        log.info("userId : " + this.userId);
        return Lists.builder()
                .name(this.name)
                .user(user)
                .build();
    }
}
