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

    // 채팅방 ( topic ) 에 발행되는 메세지를 처리할 Listener
    private final RedisMessageListenerContainer redisMessageListener;

    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String,Object> redisTemplate;
    private HashOperations<String,String,ChatRoom> opsHashChatRoom;

    /**
     * 채팅방의 대화 메세지를 발행하기 위한 redis topic정보
     * 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을 수 있도록 한다.
     */
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init(){
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom(){
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

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

    /**
     * 채팅방 입장 : redis에 topic을 만들고 Pub/sub 통신을 하기위해 리스너를 설정
     */
     public void enterChatRoom(String roomId){
         ChannelTopic topic = topics.get(roomId);
         if(topic == null){
             topic = new ChannelTopic(roomId);
             redisMessageListener.addMessageListener(redisSubscriber,topic);
             topics.put(roomId,topic);
         }
     }

     public ChannelTopic getTopic(String roomId){
         return topics.get(roomId);
     }

}
