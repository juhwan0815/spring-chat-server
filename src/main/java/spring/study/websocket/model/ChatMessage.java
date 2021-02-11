package spring.study.websocket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    // 메세지 타입 : 입장, 퇴장
    public enum MessageType{
        ENTER,TALK
    }


    private MessageType type; // 메세지 타입
    private String roomId; // 방번호
    private String sender; // 메세지 보낸 사람
    private String message; // 메세지

}
