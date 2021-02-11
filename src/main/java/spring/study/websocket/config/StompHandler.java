package spring.study.websocket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.repository.ChatRoomRepository;
import spring.study.websocket.service.ChatService;
import spring.study.websocket.service.JwtTokenProvider;

import java.security.Principal;
import java.util.Optional;

/**
 * 클라이언트의 입장/퇴장 이벤트를 서버에서 체크하여 메세지를 전송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     * 웹 소켓을 통해 들어온 요청이 처리되기 전 실행
     * 채팅방 입장 시 이벤트 : StompCommand.SUBSCRIBE
     * 채팅방 퇴장 시 이벤트 : StompCommand.DISCONNECT
     * 채팅방 입장/퇴장 시 채팅룸의 인원수 +- 처리
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 웹 소켓 연길 시 헤더의 jwt Token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);

            // Header의 jwt Token 검증
            jwtTokenProvider.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅방 구독 요청

            // header 정보에서 구독 destination 정보를 얻고 , roomId를 추출
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message
                    .getHeaders()
                    .get("simpDestination"))
                    .orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 매핑해놓는다.
            // 나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);

            // 채팅방에 인원수를 +1 한다.
            chatRoomRepository.plusUserCount(roomId);

            // 클라이언트 입장 메세지를 채팅방에 발송 (redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName).orElse("UnKnownUser");
            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(name)
                    .build());

            log.info("SUBSCRIBED {},{}", name, roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // 웹 소켓 연결 종료

            // 연결이 종료된 클라이언트 sessionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);

            // 채팅방 전체 인원수를 -1 한다.
            chatRoomRepository.minusUserCount(roomId);

            // 클라이언트의 퇴장 메세지를 채팅방에 발송한다 ( redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName).orElse("UnKnownUser");

            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.QUIT)
                    .roomId(roomId)
                    .sender(name)
                    .build());

            // 퇴장한 클라이언트의 roomId 맵핑정보를 삭제한다.
            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {},{}",sessionId,roomId);
        }
        return message;
    }
}
