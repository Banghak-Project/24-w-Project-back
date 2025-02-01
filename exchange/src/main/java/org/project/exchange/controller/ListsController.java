package org.project.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.project.exchange.model.list.Dto.ListsRequestDto;
import org.project.exchange.model.list.Dto.ListsResponseDto;
import org.project.exchange.model.list.service.ListsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
public class ListsController {
    private final ListsService listsService;

    // 모든 리스트 조회
    @GetMapping
    public List<ListsResponseDto> getAllLists() {
        return listsService.showAllLists();
    }

    // 리스트 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createList(@RequestBody ListsRequestDto requestDto) {
        listsService.createList(requestDto);
    }

    // 리스트 삭제
    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable Long id) {
        listsService.deleteList(id);
    }
}
