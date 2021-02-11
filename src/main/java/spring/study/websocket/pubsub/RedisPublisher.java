package spring.study.websocket.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import spring.study.websocket.model.ChatMessage;

/**
 * Redis 발행 서비스
 * 채팅방에 입장하여 메세지를 작성하면 해당 메세지를 redis topic에 발행하는 기능의 서비스
 * 이 서비스를 통해 메세지를 발행하면 대기하고 있던 redis 구독 서비스가 메세지를 처리
 */
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String,Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessage message){
        redisTemplate.convertAndSend(topic.getTopic(),message);
    }
}
