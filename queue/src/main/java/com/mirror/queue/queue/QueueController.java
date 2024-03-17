package com.mirror.queue.queue;

import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class QueueController {
    private static final int PORT = 5000;
    private static final String HOSTNAME = "192.168.1.185";
    private static SongQueue sq = new SongQueue();
    private static PiClient pi;

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(
        @Payload Request userRequest
    ) {
        try {
            pi = new PiClient(HOSTNAME, PORT);
            // Send Queue to New User
            if (userRequest.getSongName().equals("NULL")) {
                return sq;
            }
            
            var id = "00000";
            // Add song to Queue
            Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
            sq.push(newSong);
            sq.printQueue();
            pi.sendMessage(userRequest.getSongName());
            // Return payload
            // return userRequest;
            return sq;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
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
