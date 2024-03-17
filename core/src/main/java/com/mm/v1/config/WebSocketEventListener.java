package com.mm.v1.config;

import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mm.v1.queue.MessageType;
import com.mm.v1.queue.Request;

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
