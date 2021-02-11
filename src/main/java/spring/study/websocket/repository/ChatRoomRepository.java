package spring.study.websocket.repository;

import org.springframework.stereotype.Repository;
import spring.study.websocket.model.ChatRoom;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 채팅방을 생성하고 정보를 조회하는 리포지토리
 */
@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init(){
        chatRoomMap = new LinkedHashMap<>();
    }

    public List<ChatRoom> findAllRoom(){
        // 채팅방 생성순서 최근 순으로 반환
        List chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoom findRoomById(String id){
        return chatRoomMap.get(id);
    }

    public ChatRoom createChatRoom(String name){
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomMap.put(chatRoom.getRoomId(),chatRoom);
        return chatRoom;
    }


}
