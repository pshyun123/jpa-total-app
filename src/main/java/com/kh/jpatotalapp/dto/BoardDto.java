package com.kh.jpatotalapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
// 리액트와 Json 서로의 키 값이 됨
@Getter
@Setter
public class BoardDto {
private Long boardId;
private String email;
private Long categoryId;
private String title;
private String content;
private String img;
private LocalDateTime regDate;
}
