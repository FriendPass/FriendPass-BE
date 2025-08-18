package com.likelion.friendpass.config;

import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 클라 connectHeaders에 user-id를 넣어주세요.
            String userIdStr = accessor.getFirstNativeHeader("user-id");
            if (userIdStr != null) {
                try {
                    Long userId = Long.parseLong(userIdStr);
                    var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                    accessor.setUser(auth);
                } catch (NumberFormatException ignore) { /* 잘못된 값이면 인증 미설정 */ }
            }
        }
        return message;
    }
}
