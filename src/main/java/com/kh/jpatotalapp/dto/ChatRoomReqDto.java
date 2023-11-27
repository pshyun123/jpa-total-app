package com.kh.jpatotalapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomReqDto {// 방 만들기 위해 요청(이메일, 이름)을 보냄
    private String email;
    private String name;
}