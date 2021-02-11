package spring.study.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring.study.websocket.model.ChatRoom;
import spring.study.websocket.service.ChatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 생성
     */
    @PostMapping("/chat")
    public ChatRoom createRoom(@RequestParam String name){
        return chatService.createRoom(name);
    }

    /**
     * 채팅방 조회
     */
    @GetMapping("/chat")
    public List<ChatRoom> findAllRoom(){
        return chatService.findAllRoom();
    }
}
