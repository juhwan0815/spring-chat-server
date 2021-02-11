package spring.study.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import spring.study.websocket.pubsub.RedisSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    /**
     * ChannelTopic을 단일화
     *
     * 단일 Topic 사용을 위한 Bean 설정
     */
    @Bean
    public ChannelTopic channelTopic(){
        return new ChannelTopic("chatroom");
    }

    /**
     * redis pub/sub 메세지를 처리하는 listener 설정
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              ChannelTopic channelTopic){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 메세지 리스너 단일화
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    /**
     * 메세지를 구독자에게 보내는 역할
     * 실제 메세지를 처리하는 subscriber 설정 추가
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber){
        return new MessageListenerAdapter(subscriber,"sendMessage");
    }

    /**
     * 어플리케이션에서 사용할 redisTemplate 설정
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }
}
