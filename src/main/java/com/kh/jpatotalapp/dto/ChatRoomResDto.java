package com.kh.jpatotalapp.dto;
import com.kh.jpatotalapp.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Slf4j
public class ChatRoomResDto {
    private String roomId;
    private String name;
    private LocalDateTime regDate;

    @JsonIgnore // 이 어노테이션으로 WebSocketSession의 직렬화를 방지
    private Set<WebSocketSession> sessions; // 채팅방에 입장한 세션 정보를 담을 Set/ 한방에 세션을 묶어두는 역할
    // 세션 수가 0인지 확인하는 메서드
    public boolean isSessionEmpty() { // 방 없앨 때는 세션 수 확인
        return this.sessions.size() == 0; // size가 총 세션의 개수를 의미. 연결된 세션 하나도 없으면 true->방폭파, 있으면 false-> 방폭파 X, 사람이 나갈때마다 체크하는 곳
    }

    @Builder// 빌더 패턴 적용
    public ChatRoomResDto(String roomId, String name, LocalDateTime regDate) { // 이 정보가 들서비스에 들어감
        this.roomId = roomId;
        this.name = name;
        this.regDate = regDate;
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

}