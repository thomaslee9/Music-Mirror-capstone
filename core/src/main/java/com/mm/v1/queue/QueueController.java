package com.mm.v1.queue;

import org.springframework.stereotype.Controller;

import com.mm.v1.SeedBuilder;
import com.mm.v1.SpotifyPlaybackController;
import com.mm.v1.artist.ArtistObject;
import com.mm.v1.requests.RecommendationRequest;
import com.mm.v1.responses.RecommendationResponse;
import com.mm.v1.song.TrackObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            String access_token = "BQAjPGdwh2CTV_4pQPlGAEvaTJRYNvLUBN3QJoOJbsyRJxCrc8FuVvA1OrOjCkCzOyGmx38IsXXggurwQz6vlNcZ9E0a1en67WKJpY_WhCYIZTLCLY8gFlueKh-SNZhnbcTUdfSj5KoP2am7XGbcSk-YjoGiTkioWfjHHh5joGHgmBFqQMj3CZgeEYrB";
            
            SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);

            String id = "00000";

            String song_name = userRequest.getSongName();
            String artist_name = userRequest.getSongArtist();

            // if [REC] is appended to the song name it means we want a rec
            if (song_name.contains("!REC")) {

                /** NOTE:
                 * 
                 * in the actual system, this pi will communicate with the
                 * second pi to receive the recommendation seed
                 *
                 */

                // parse the song without the [REC] appended

                String cleaned_name = song_name.replaceAll("!REC", "");
                TrackObject track = P.getSong(cleaned_name, artist_name);

                // now that we have the track, get the id, artist_id, and genre
                String song_id = track.getId();
                String artist_id = track.getFirstArtistId();
                String[] genres = track.getGenres();

                System.out.println("### Generating Recommendation for: ###");
                System.out.println("# Song_ID = " + song_id + " #");
                System.out.println("# Artist_ID = " + artist_id + " #");
                System.out.print("# Genres = ");
                for (String genre : genres) {
                    System.out.print(genre + " ");
                }
                System.out.println("#");

                /**
                 * 
                 * we would then send this info to the rec pi, and are returned
                 * with a song_id to queue
                 * 
                 */

                System.out.println("(Would be queuing the returned song)");

                //System.out.println("### Queuing Song ###");
                // Add song to Queue
                //Song newSong = new Song(name, artist_string, id, userRequest.getUser());
                //sq.push(newSong);
                //sq.printQueue();
                //P.queueSong(song_id);

            }
            // otherwise just queue the song as normal
            else    {
                System.out.println("### Queuing Song ###");
                // Add song to Queue
                Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), id, userRequest.getUser());
                sq.push(newSong);
                sq.printQueue();
                P.queueSong(song_name, artist_name);
            }

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
