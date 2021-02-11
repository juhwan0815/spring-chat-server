package spring.study.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.model.ChatRoom;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * @MessageMapping을 통해 웹 소켓으로 들어오는 메세지 발행을 처리
     * /pub/chat/message 로 발행요청을 하면 controller가 해당 메세지를 받아 처리
     * 메세지가 발행되면 /sub/chat/room/{roomId}로 메세지를 전송
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message){
        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
