package org.project.exchange.model.list.service;

import lombok.RequiredArgsConstructor;
import org.project.exchange.model.list.Dto.ListsRequestDto;
import org.project.exchange.model.list.Dto.ListsResponseDto;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.list.repository.ListsRepository;
import org.project.exchange.model.user.User;
import org.project.exchange.model.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ListsService {
    private final ListsRepository listsRepository;
    private final UserRepository userRepository;
    /**
     * List 구현해야 할것
     * 1. 모든 리스트 보여주기
     * 2. 리스트 생성
     * 3. 리스트 삭제
     */

    public List<ListsResponseDto> showAllLists() {
        return listsRepository.findAll()
                .stream()
                .map(ListsResponseDto::new)
                .collect(Collectors.toList());
    }

    public Lists createList(ListsRequestDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Lists newLists = requestDto.toEntity(user);
        return listsRepository.save(newLists);
    }
    public void deleteList(Long id) {listsRepository.deleteById(id);}
}
