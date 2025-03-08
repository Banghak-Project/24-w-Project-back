package org.project.exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.global.api.ApiResponse;
import org.project.exchange.model.list.Dto.CreateRequest;
import org.project.exchange.model.list.Dto.ListsResponseDto;
import org.project.exchange.model.list.Dto.UpdateRequest;
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

    //새로운 리스트 추가
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Lists>> createList(@RequestBody ListsRequestDto requestDto) {
        Lists newLists = listsService.createList(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccessWithMessage(newLists, "리스트 추가 성공"));
    }

    //모든 리스트 불러오기
    @GetMapping
    public ResponseEntity<ApiResponse<List<ListsResponseDto>>> getAllLists() {
        List<ListsResponseDto> lists = listsService.showAllLists();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(lists, "리스트 조회 성공"));
    }
    

    //특정 리스트 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteList(@PathVariable Long id) {
        listsService.deleteList(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.createSuccessWithMessage(null, "리스트 삭제 성공"));
    }

    //총금액표시
    @GetMapping("/total/{id}")
    public ResponseEntity<ApiResponse<Double>> getTotal(@PathVariable Long id) {
        double total = listsService.getTotal(id);
      
    //환율 변경
    @PatchMapping("/update/{id}")
    public ResponseEntity<Lists> updateCurrency(@PathVariable Long id, @RequestBody UpdateRequest requestDto) {
        Lists updatedLists = listsService.updateList(id, requestDto);
        return ResponseEntity.ok(updatedLists);
      return ResponseEntity.ok(ApiResponse.createSuccessWithMessage(updatedLists, "환율 변경 성공"));}
    }
}

