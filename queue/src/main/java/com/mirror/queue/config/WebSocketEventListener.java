package com.mirror.queue.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mirror.queue.queue.Request;
import com.mirror.queue.queue.MessageType;

import org.springframework.messaging.simp.SimpMessageSendingOperations;


@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    

    @EventListener
    public void handleWebSocketDisconnectListener(
        SessionDisconnectEvent event
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        // Extract username of disconnector
        String username = (String) accessor.getSessionAttributes().get("username");

        if (username != null) {
            log.info("User Disconnected from the Queue: " + username);

            var userRequest = Request.builder()
                .type(MessageType.LEAVE)
                .username(username)
                .build();

            messagingTemplate.convertAndSend("/topic/public", userRequest);   

        }
    }
}
