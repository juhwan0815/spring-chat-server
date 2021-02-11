package spring.study.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.repository.ChatRoomRepository;
import spring.study.websocket.service.JwtTokenProvider;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisTemplate<String,Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelTopic channelTopic;

    /**
     * @MessageMapping을 통해 웹 소켓으로 들어오는 메세지 발행을 처리
     *  웹 소켓 "/pub/chat/message" 로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token){
        String nickname = jwtTokenProvider.getUsernameFromJwt(token);

        // 로그인 화면 정보로 대화명설정
        message.setSender(nickname);

        // 채팅방 입장시에는 대화명과 메세지를 자동으로 세팅한다.
        if(ChatMessage.MessageType.ENTER.equals(message.getType())){
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // 웹 소켓에 발행된 메세지를 redis로 발행한다. publish
        redisTemplate.convertAndSend(channelTopic.getTopic(),message);
    }

}
