package com.kh.jpatotalapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.jpatotalapp.dto.ChatMessageDto;
import com.kh.jpatotalapp.dto.ChatRoomResDto;
import com.kh.jpatotalapp.entity.Chat;
import com.kh.jpatotalapp.entity.ChatRoom;
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
    private final ObjectMapper objectMapper; // JSON 문자열로 변환하기 위한 객체
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private Map<String, ChatRoomResDto> chatRooms; // 채팅방 리스트를 담을 맵 키:고유방번호, 밸류:ChatRoomResDto

    @PostConstruct // 의존성 주입 이후 초기화를 수행하는 메소드
    private void init() { // 채팅방 정보를 담을 맵을 초기화
        chatRooms = new LinkedHashMap<>(); // 채팅방 정보를 담을 맵- 채팅방을 순서대로 정렬됨
    }
    public List<ChatRoomResDto> findAllRoom() { // 채팅방 리스트 반환
        return new ArrayList<>(chatRooms.values());
    }
    public ChatRoomResDto findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    // 방 개설하기
    public ChatRoomResDto createRoom(String name) {
        String randomId = UUID.randomUUID().toString(); //랜덤한 방 고유번호 생성
        log.info("UUID : " + randomId);// 생성된 번호 찍어주는 것
        ChatRoomResDto chatRoom = ChatRoomResDto.builder() // 채팅방 생성 빌더가 dto의 @Builder 있는 쪽의 정보 가져옴
                .roomId(randomId)
                .name(name)
                .regDate(LocalDateTime.now())
                .build();
        chatRooms.put(randomId, chatRoom);  // 채팅방들의 모음 / 방 생성, 랜덤 키값이 UUID로 하고 방 정보를 값으로 저장
        //DB에 채팅방 저장 - 생성과 동시에

        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setRoomId(chatRoom.getRoomId());
        newChatRoom.setRoomName(chatRoom.getName());
        newChatRoom.setCreatedAt(chatRoom.getRegDate());
        chatRoomRepository.save(newChatRoom);

        return chatRoom;

    }
    public void removeRoom(String roomId) { // 방 삭제
        ChatRoomResDto room = chatRooms.get(roomId); // 방 정보 가져오기
        if (room != null) { // 방이 존재하면
            if (room.isSessionEmpty()) { // 방에 세션이 없으면
                chatRooms.remove(roomId); //맵에서 방 삭제
                //DB에서도 삭제
                chatRoomRepository.deleteById(roomId); // ManyToOne의 one의 역할이 roomId이다. many에 해당하는 애들이 사라짐
            }
        }
    }
    // 채팅방에 입장한 세션 추가 / 타입이 엔터면~
    public void addSessionAndHandleEnter(String roomId, WebSocketSession session, ChatMessageDto chatMessage) {
        ChatRoomResDto room = findRoomById(roomId);
        if (room != null) {
            room.getSessions().add(session); // 채팅방에 입장한 세션 추가
            if (chatMessage.getSender() != null) { // 채팅방에 입장한 사용자가 있으면
                chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장했습니다.");// 메세지 여기서 바꿈
                sendMessageToAll(roomId, chatMessage); // 채팅방에 입장 메시지 전송
            }
            log.debug("New session added: " + session);
        }
    }
    // 채팅방에서 퇴장한 세션 제거
    public void removeSessionAndHandleExit(String roomId, WebSocketSession session, ChatMessageDto chatMessage) {
        ChatRoomResDto room = findRoomById(roomId); // 채팅방 정보 가져오기
        if (room != null) {
            room.getSessions().remove(session); // 채팅방에서 퇴장한 세션 제거
            if (chatMessage.getSender() != null) { // 채팅방에서 퇴장한 사용자가 있으면
                chatMessage.setMessage(chatMessage.getSenderName() + "님이 퇴장했습니다.");// 이메일 아니라 닉네임으로 정보가 보여짐
                sendMessageToAll(roomId, chatMessage); // 채팅방에 퇴장 메시지 전송
            }
            log.debug("Session removed: " + session);
            if (room.isSessionEmpty()) {
                removeRoom(roomId);
            }
        }
    }
// 한명이 채팅 쳐도 다른 사람에게 보여지도록
    public void sendMessageToAll(String roomId, ChatMessageDto message) { // dto에 메세지 담김.
        ChatRoomResDto room = findRoomById(roomId);//룸아이디로 해당하는 방 찾기
        if (room != null) { //룸이 존재하면
            for (WebSocketSession session : room.getSessions()) { //향상된 for문으로 세션정보를 불러옴. 방안의 각각의 세션에 메세지를 뿌려주기 위해서
                sendMessage(session, message);
            }
            saveMessage(roomId, message); // 메세지를 db에 한번만 저장하기 위해서
        }
    }
// 메세지를 보내는 작업 자체
    public <T> void sendMessage(WebSocketSession session, T message) { // 해당 세션의 메세지 정보를 보내줌
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message))); // 객채(dto)를  json형식으로 바꿈(string으로 바꿈)
        } catch(IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    //dto->entity
    public void saveMessage(String roomId, ChatMessageDto chatMessageDto){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 존재하지 않습니다."));
        Chat chatMessage = new Chat();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setMessage(chatMessageDto.getMessage());
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessage.setSenderName(chatMessageDto.getSenderName());
        chatRepository.save(chatMessage);
    }
}