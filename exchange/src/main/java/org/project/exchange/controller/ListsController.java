package org.project.exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.model.list.Dto.ListsRequestDto;
import org.project.exchange.model.list.Dto.ListsResponseDto;
import org.project.exchange.model.list.Lists;
import org.project.exchange.model.list.service.ListsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ListsController {
    private final ListsService listsService;

    @PostMapping("/add")
    public ResponseEntity<Lists> createList(@RequestBody ListsRequestDto requestDto) {
        System.out.println("Received request to create list: " + requestDto.getName());
        Lists newLists = listsService.createList(requestDto);
        System.out.println("Created new list with ID: " + newLists.getListId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newLists);
    }

    @GetMapping
    public ResponseEntity<List<ListsResponseDto>> getAllLists() {
        List<ListsResponseDto> lists = listsService.showAllLists();
        return ResponseEntity.ok(lists);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        listsService.deleteList(id);
        return ResponseEntity.noContent().build();//204 No Content반환
    }
    //총금액표시

    //일 소비 총액 표시
}

