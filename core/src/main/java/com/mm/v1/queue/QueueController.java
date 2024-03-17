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

            String access_token = "BQDF57aYkkGIIYLnMPBFaSKI--qhJXW4ZHQ81iufvO9O2f9ePZ9ICraLNdXvsb7D-pbQsGyk2HnsOXxivT9wV0q2o0MeDxDk4FbiRkh1aUFOq4sd9K-0a2kUeuCXVT0NTwjQtIc23X6A8uQxcMNAuaBhOhvwu_odzBfcItixwzAwFdRepWvSvdRX4Tm8";
            
            SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);

            String id = "00000";

            String song_name = userRequest.getSongName();
            String artist_name = userRequest.getSongArtist();

            // if [REC] is appended to the song name it means we want a rec
            if (song_name.contains("[REC]")) {

                System.out.println("### Getting Recommendation ###");
                SeedBuilder builder = new SeedBuilder("3WrFJ7ztbogyGnTHbHJFl2", "rock", "2hOC9qItvmSkgMnxRjgPSr");
                builder.addMinAcousticness("0.1");
                builder.addMinPopularity("75");
                builder.addMaxDanceability("0.5");

                String seed = builder.getSeed();
                System.out.println(seed);

                RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);

                TrackObject recommended_song = rec.getTracks()[0];
                String song_id = recommended_song.getId();

                String name = recommended_song.getName();

                List<String> curr_artist_names = new ArrayList<String>();
                // get the resulting artists for the track search result
                for (ArtistObject artist : recommended_song.getArtists()) {
                    curr_artist_names.add(artist.getName());
                }
                String artist_string = "";
                for (String curr_artist : curr_artist_names) {
                    artist_string += curr_artist + " ";
                }

                System.out.println("### Queuing Song ###");
                // Add song to Queue
                Song newSong = new Song(name, artist_string, id, userRequest.getUser());
                sq.push(newSong);
                sq.printQueue();
                P.queueSong(song_id);

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
