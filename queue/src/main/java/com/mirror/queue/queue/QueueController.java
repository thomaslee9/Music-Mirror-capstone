package com.mirror.queue.queue;

import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class QueueController {

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/main")
    public Request sendRequest(
        @Payload Request userRequest
    ) {
        // Return User Request
        return userRequest;
    }

    @MessageMapping("/queue.addUser")
    @SendTo("/topic/main")
    public Request addUser(
        @Payload Request userRequest,
        SimpMessageHeaderAccessor accessor
    ) {
        // Accept a new user
        accessor.getSessionAttributes().put("username", userRequest.getUser());
        return userRequest;
    }

}
