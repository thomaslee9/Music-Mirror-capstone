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
    // Vote Decisions
    private static final int NOTHING = 0;
    private static final int LIKE = 1;
    private static final int DISLIKE = 2;
    // Connection
    private static final int PORT = 5000;
    private static final String HOSTNAME = "192.168.1.185";
    // Song Queue 
    private static SongQueue sq = new SongQueue();
    private static SongDict sd = new SongDict();
    private static int curSongID = 0;
    // Raspberry Pi
    private static PiClient pi;
    private static boolean pi_active = false;

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(@Payload Request userRequest) {

        // Host on Raspberry Pi 
        if (pi_active)  {

            try {
                // Activate Pi CLient
                pi = new PiClient(HOSTNAME, PORT);

                // Send Queue to New User on Connect
                if (userRequest.getSongName().equals("NULL")) {
                    return sq;
                }

                // =============================================================
                // QUEUE MANAGER SECTION
                // Set Song ID
                String id = "00000";
                // Add song to Queue
                Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
                sd.add(newSong);
                sq.push(newSong);
                sq.printQueue();
                // =============================================================

                // =============================================================
                // SPOTIFY WEB API SECTION 
                String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
                SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);
                System.out.println("### Queuing Song ###");
                String song_name = userRequest.getSongName();
                String artist_name = userRequest.getSongArtist();
                // Queue Song on Spotify Web API
                P.queueSong(song_name, artist_name);
                // =============================================================

                // Transmit on Pi
                pi.sendMessage(userRequest.getSongName());

                // Return userRequest
                return sq;

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

        // Host on Local Device
        } else {

            // Send Queue to New User on Connect
            if (userRequest.getSongName().equals("NULL")) {
                return sq;
            }

            // =================================================================
            // QUEUE MANAGER SECTION
            // Set Song ID
            String id = String.valueOf(curSongID);
            curSongID += 1;
            // Add song to Queue
            Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
            sd.add(newSong);
            sq.push(newSong);
            sq.printQueue();
            // =================================================================

            // =================================================================
            // SPOTIFY WEB API SECTION
            // String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
            // SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);
            // System.out.println("### Queuing Song ###");
            // String song_name = userRequest.getSongName();
            // String artist_name = userRequest.getSongArtist();
            // // Queue Song on Spotify Web API
            // P.queueSong(song_name, artist_name);
            // =================================================================
            
            // Return userRequest
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

    @MessageMapping("/queue.sendLike")
    @SendTo("/topic/remove")
    public String sendLike(
        @Payload Vote userVote
    ) {
        // Extract Vote details
        String id = userVote.getSongId();
        int vote = userVote.getVote();

        // Process Vote
        if (vote == LIKE) {
            // Like Song
            sd.like(id);
        } else if (vote == DISLIKE) {
            // Dislike Song
            int numLikes = sd.dislike(id);
            // Check if Song has been vetoed
            if (numLikes < 0) {
                Song song = sd.getSongById(id);
                sq.remove(song);
                sd.removeById(id);
                // Return vetoed Song ID
                return id;
            }
        }

        // Return something
        return "none";
    }

}
