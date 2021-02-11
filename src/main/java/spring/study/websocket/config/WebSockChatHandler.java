package spring.study.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.model.ChatRoom;
import spring.study.websocket.service.ChatService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSockChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    /**
     * 웹 소켓 클라이언트로부터 채팅 메세지를 전달받아 채팅 메세지 객체로 변환
     * 전달받은 메세지에 담긴 채팅방 Id로 발송 대상 채팅방 정보를 조회
     * 해당 채팅방에 입장해있는 모든 클라이언트들에게 타입에 따른 메세지 발송
     */
    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}",payload);

        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());

        room.handleActions(session,chatMessage,chatService);
    }
}
