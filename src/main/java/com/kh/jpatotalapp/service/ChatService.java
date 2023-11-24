package com.kh.jpatotalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.jpatotalapp.dto.ChatRoomResDto;
import com.kh.jpatotalapp.entity.ChatRoom;
import com.kh.jpatotalapp.entity.Chat;
import com.kh.jpatotalapp.repository.ChatRepository;
import com.kh.jpatotalapp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper;
    private Map<String, ChatRoomResDto> chatRooms;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct // 의존성 주입 이후 초기화를 수행하는 메소드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }
    public List<ChatRoomResDto> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }
    public ChatRoomResDto findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    // 방 개설하기
    public ChatRoomResDto createRoom(String name) {
        String randomId = UUID.randomUUID().toString();
        log.info("UUID : " + randomId);
        ChatRoomResDto chatRoom = ChatRoomResDto.builder()
                .roomId(randomId)
                .name(name)
                .regDate(LocalDateTime.now())
                .build();
        ChatRoom chatRoomEntity = new ChatRoom();
        chatRoomEntity.setRoomId(randomId);
        chatRoomEntity.setRoomName(name);
        chatRoomEntity.setCreatedAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoomEntity);
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }
    public void removeRoom(String roomId) {
        ChatRoomResDto room = chatRooms.get(roomId);
        if (room != null) {
            if (room.isSessionEmpty()) {
                chatRooms.remove(roomId);
            }
        }
    }
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch(IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    // 채팅 메세지 DB저장
    public void saveMessage(String roomId, String sender, String message) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 존재하지 않습니다."));
        Chat chatMessage = new Chat();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setSentAt(LocalDateTime.now());
        chatRepository.save(chatMessage);
    }
    // 이전 채팅 가져오기
    public List<Chat> getRecentMessages(String roomId) {
        return chatRepository.findRecentMessages(roomId);
    }
}