package spring.study.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import spring.study.websocket.model.ChatMessage;
import spring.study.websocket.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;


    /**
     * destination 정보에서 roomId 추출
     */
    public String getRoomId(String destination){
        int lastIndex = destination.lastIndexOf("/");
        if(lastIndex != -1){
            return destination.substring(lastIndex + 1);
        }
        else
            return "";
    }

    /**
     * 채팅방에 메세지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage){

        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));

        if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())){
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장하셨습니다.");
            chatMessage.setSender("[알림]");

        }else if(ChatMessage.MessageType.QUIT.equals(chatMessage.getType())){
            chatMessage.setMessage(chatMessage.getMessage() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(),chatMessage);
    }

}
