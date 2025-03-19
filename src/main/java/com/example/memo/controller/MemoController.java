package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/memos")
public class MemoController {
  private final Map<Long, Memo> memoList = new HashMap<>();

  @PostMapping
  public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {
    // 식별자가 1 씩 증가.
    Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

    // 요청받은 데이터로 Memo 객체 생성
    Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

    // InMemory DB 에 Memo 메모
    memoList.put(memoId,memo);

    return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<MemoResponseDto>> findAllMemos() {
    // init List
    List<MemoResponseDto> responseList = new ArrayList<>();

    responseList = memoList.values().stream().map(MemoResponseDto::new).toList();

    return new ResponseEntity<>(responseList, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {
    Memo memo = memoList.get(id);
    if (memo == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MemoResponseDto> updateMemoById(
          @PathVariable Long id,
          @RequestBody MemoRequestDto dto
  ) {
    Memo memo = memoList.get(id);

    // NPE 방지
    if (memo == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 필수값 검증
    if (dto.getTitle() == null || dto.getContents() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    memo.update(dto);

    return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK) ;
  }

  @PatchMapping("/{id}")
  public ResponseEntity<MemoResponseDto> updateTitle(
          @PathVariable Long id,
          @RequestBody MemoRequestDto dto
  ) {
    Memo memo = memoList.get(id);

    // NPE 방지
    if (memo == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 수정할 데이터가 없을 경우 거나 content 값이 잇을 겨우
    if (dto.getTitle() == null || dto.getContents() != null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    memo.updateTitle(dto);

    return new ResponseEntity<>(new MemoResponseDto(memo),HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

    if (memoList.containsKey(id)) {
      memoList.remove(id);

      return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }


}
