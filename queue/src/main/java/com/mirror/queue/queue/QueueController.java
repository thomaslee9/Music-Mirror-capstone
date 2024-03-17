package com.mirror.queue.queue;

import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class QueueController {
    private final int PORT = 5000;
    private final String HOSTNAME = "192.168.1.185";
    private static SongQueue sq = new SongQueue();
    private static PiClient pi = new PiClient(HOSTNAME, PORT);

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(
        @Payload Request userRequest
    ) {

        // Send Queue to New User
        if (userRequest.getSongName().equals("NULL")) {
            return sq;
        }
        
        var id = "00000";
        // Add song to Queue
        Song newSong = new Song(userRequest.getSongName(), id, userRequest.getUser());
        sq.push(newSong);
        sq.printQueue();
        pi.sendMessage(userRequest.getSongName());
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
