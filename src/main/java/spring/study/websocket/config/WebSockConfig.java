package spring.study.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket // 웹 소켓 활성화
public class WebSockConfig implements WebSocketConfigurer {

    private final WebSockChatHandler webSockChatHandler;

    /**
     * 웹 소켓에 접속하기 위한 endPoint /ws/chat 으로 설정
     * 다른 서버에서도 접속이 가능하도록 cors 설정
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSockChatHandler,"/ws/chat")
                .setAllowedOrigins("*");
    }
}
