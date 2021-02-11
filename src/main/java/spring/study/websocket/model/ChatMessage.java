package spring.study.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    public ChatMessage() {
    }

    // 메세지 타입 : 입장, 퇴장, 채팅
    public enum MessageType{
        ENTER,TALK,QUIT
    }

    private MessageType type; // 메세지 타입
    private String roomId; // 방번호
    private String sender; // 메세지 보낸 사람
    private String message; // 메세지
    private long userCount; // 채팅방 인원수, 채팅방 내에서 메세지가 전달될때 인원수 갱신시 사용

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.userCount = userCount;
    }
}
