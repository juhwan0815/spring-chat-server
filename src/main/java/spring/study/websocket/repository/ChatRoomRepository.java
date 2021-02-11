package spring.study.websocket.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import spring.study.websocket.model.ChatRoom;
import spring.study.websocket.pubsub.RedisSubscriber;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 채팅방을 생성하고 정보를 조회하는 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String,Object> redisTemplate;
    private HashOperations<String,String,ChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init(){
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRoom(){
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String id){
        return opsHashChatRoom.get(CHAT_ROOMS,id);
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장
     */
    public ChatRoom createChatRoom(String name){
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS,chatRoom.getRoomId(),chatRoom);
        return chatRoom;
    }
}
