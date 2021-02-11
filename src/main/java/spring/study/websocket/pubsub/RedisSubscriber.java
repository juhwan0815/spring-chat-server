package spring.study.websocket.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import spring.study.websocket.model.ChatMessage;

/**
 * Redis 구독 서비스 구현
 * Redis에 메세지 발행이 될 때까지 대기하였다가 메세지가 발행되면 해당 메세지를 읽어 처리하는 리스너
 * 1. Redis에 메세지가 발행되면 해당 메세지를 ChatMessage로 변환
 * 2. messaging Template를 이용하여 채팅방의 모든 웹소켓 클라이언트들에게 메세지를 전달
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메세지가 발행되면 대기하고 있던 onMessage가 해당 메세지를 받아 처리
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate
                    .getStringSerializer()
                    .deserialize(message.getBody());
            // ChatMessage 객체로 매핑
            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            // 웹 소켓 구독자에게 채팅 메세지 send
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(),roomMessage);

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
