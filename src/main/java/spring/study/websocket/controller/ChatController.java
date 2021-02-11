package spring.study.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.model.ChatRoom;
import spring.study.websocket.pubsub.RedisPublisher;
import spring.study.websocket.repository.ChatRoomRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * @MessageMapping을 통해 웹 소켓으로 들어오는 메세지 발행을 처리
     *  웹 소켓 "/pub/chat/message" 로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message){
        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // 웹 소켓에 발행된 메세지를 redis로 발행한다. publish
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()),message);
    }

}
