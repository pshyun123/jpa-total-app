package com.kh.jpatotalapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.jpatotalapp.dto.ChatMessageDto;
import com.kh.jpatotalapp.dto.ChatRoomResDto;
import com.kh.jpatotalapp.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//핸들러 : ENTER가 들어오면 서비스에 명령전달
@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

private final ObjectMapper objectMapper;
private final ChatService chatService;
private final Map<WebSocketSession, String> sessionRoomIdMap = new ConcurrentHashMap<>();
@Override
protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { // 들어오면서 세션 생기고 연결됨. 들어옴과 동시에
        // 리액트의 ws.current => 자바의 WebSocketSession
        // 리액트의 JSON.stringify의 내용들이 => 자바의 TextMessage message

        String payload = message.getPayload();
        log.warn("{}", payload); //payload: 키밸류를 가져옴. json데이터를 받아옴
        ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);//objectMapper: json를 dto(객체)로 변환
        String roomId = chatMessage.getRoomId();
        // 세션과 채팅방 ID를 매핑
        sessionRoomIdMap.put(session, chatMessage.getRoomId());
        if (chatMessage.getType() == ChatMessageDto.MessageType.ENTER) {
                chatService.addSessionAndHandleEnter(roomId, session, chatMessage);//서비스로 연동
        } else if (chatMessage.getType() == ChatMessageDto.MessageType.CLOSE) {
                chatService.removeSessionAndHandleExit(roomId, session, chatMessage);
        } else {
                chatService.sendMessageToAll(roomId, chatMessage);
        }
}
        @Override
        // WebSocket 연결의 생명주기를 관리하는데 있어 중요한 부분을 담당, 연결 종료 직 후 호출
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                // 세션과 매핑된 채팅방 ID 가져오기
                String roomId = sessionRoomIdMap.remove(session);
                if (roomId != null) {
                        ChatMessageDto chatMessage = new ChatMessageDto();
                        chatMessage.setType(ChatMessageDto.MessageType.CLOSE);
                        chatService.removeSessionAndHandleExit(roomId, session, chatMessage);
                }
        }
}