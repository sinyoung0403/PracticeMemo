package com.example.memo.dto;

import lombok.Getter;

// 요청 받을 데이터
@Getter
public class MemoRequestDto {

  private String title;
  private String contents;
}
