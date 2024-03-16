package com.mirror.queue.queue;

import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class QueueController {

    private static SongQueue sq = new SongQueue();

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(
        @Payload Request userRequest
    ) {

        var id = "00000";
        // Add song to Queue
        Song newSong = new Song(userRequest.getSongName(), id, userRequest.getUser());
        sq.push(newSong);
        // sq.printQueue();

        // Return payload
        // return userRequest;
        return sq;
    }

    @MessageMapping("/queue.addUser")
    @SendTo("/topic/public")
    public Request addUser(
        @Payload Request userRequest,
        SimpMessageHeaderAccessor accessor
    ) {
        // Accept a new user
        accessor.getSessionAttributes().put("username", userRequest.getUser());
        return userRequest;
    }

}
