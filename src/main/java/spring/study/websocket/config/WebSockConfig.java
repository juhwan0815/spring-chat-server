package spring.study.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker // Stomp 활성화
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * pub/sub 메세징을 구현하기 위해 메세지를 발행하는 요청의 prefix는 /pub로 시작
     * 메세지를 구독하는 요청의 prefix는 /sub로 시작하도록 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * stomp 웹 소켓의 연결 endPoint는 /ws-stomp로 설정
     * 개발 서버의 접속 주소 ws://localhost:8080/ws-stomp
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
