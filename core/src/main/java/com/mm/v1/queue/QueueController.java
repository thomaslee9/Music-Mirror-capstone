package com.mm.v1.queue;

import org.springframework.stereotype.Controller;

import com.mm.v1.SpotifyPlaybackController;

import java.io.IOException;

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
    private static boolean pi_active = false;

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(@Payload Request userRequest) {
    
        if (pi_active)  {

            try {

                pi = new PiClient(HOSTNAME, PORT);
                // Send Queue to New User
                if (userRequest.getSongName().equals("NULL")) {
                    return sq;
                }
                
                String id = "00000";
                // Add song to Queue
                Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
                sq.push(newSong);
                sq.printQueue();

                String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
                
                SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);

                System.out.println("### Queuing Song ###");

                String song_name = userRequest.getSongName();
                String artist_name = userRequest.getSongArtist();

                P.queueSong(song_name, artist_name);

                pi.sendMessage(userRequest.getSongName());
                // Return payload
                // return userRequest;
                return sq;
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        else    {

            if (userRequest.getSongName().equals("NULL")) {
                return sq;
            }
            
            String id = "00000";
            // Add song to Queue
            Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
            sq.push(newSong);
            sq.printQueue();

            String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
            
            SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);

            System.out.println("### Queuing Song ###");

            String song_name = userRequest.getSongName();
            String artist_name = userRequest.getSongArtist();

            P.queueSong(song_name, artist_name);
            return sq;
        }
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
