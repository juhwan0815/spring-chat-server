package spring.study.websocket.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import spring.study.websocket.service.ChatService;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatRoom {

    private String roomId;
    private String name;

    // 입장한 클라이언트들의 정보를 가지고 있는다.
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId,String name){
        this.roomId = roomId;
        this.name = name;
    }

    /**
     * 입장 시에 채팅룸의 session정보에 클라이언트의 session을 추가
     * 채팅룸에 메세지가 도착할 경우 채팅룸의 모든 session에 메세지를 발송
     */
    public void handleActions(WebSocketSession session,ChatMessage chatMessage,
                              ChatService chatService){
        if(chatMessage.getType().equals(ChatMessage.MessageType.ENTER)){
            sessions.add(session);
            chatMessage.setMessage(chatMessage.getSender()+"님이 입장하셨습니다.");
        }
        sendMessage(chatMessage,chatService);
    }

    public <T> void sendMessage(T message, ChatService chatService){
        sessions.parallelStream().forEach(session->chatService.sendMessage(session,message));
    }
}
