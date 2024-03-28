package com.mm.v1.queue;

import org.springframework.stereotype.Controller;

import com.mm.v1.SpotifyPlaybackController;
import com.mm.v1.communication.MessageRequest;
import com.mm.v1.communication.MessageRequestSerializer;
import com.mm.v1.song.TrackObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.*;
import java.net.*;

import org.javatuples.Pair;
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
    private static final int DISLIKE_TO_LIKE = 3;
    private static final int LIKE_TO_DISLIKE = 4;
    // Connection
    private static final int PORT = 5000;
    private static final String HOSTNAME = "192.168.1.185";
    // Song Queue 
    private static SongQueue sq = new SongQueue();
    private static SongDict sd = new SongDict();
    private static int curQueueId = 0;
    // Raspberry Pi
    //private static PiClient pi;
    private static boolean pi_active = false;

    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public SongQueue sendRequest(@Payload Request userRequest) {

        // Host on Raspberry Pi 
        if (pi_active)  {
            // Send Queue to New User on Connect
            if (userRequest.getSongName().equals("NULL")) {
                return sq;
            }

            // =============================================================
            // QUEUE MANAGER SECTION

            // Set queue ID
            String queue_id = String.valueOf(curQueueId);
            curQueueId += 1;

            // for now, the song_id will be "" until the async spotify returns w resources
            String song_id = "";

            // Add song to Queue
            Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), queue_id, song_id, userRequest.getUser());
            sd.add(newSong);
            sq.push(newSong);
            sq.printQueue();
            // =============================================================

            // =============================================================
            // < Old >  SPOTIFY WEB API SECTION
            // String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
            // SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);
            // System.out.println("### Queuing Song ###");
            // String song_name = userRequest.getSongName();
            // String artist_name = userRequest.getSongArtist();
            // Queue Song on Spotify Web API
            // P.queueSong(song_name, artist_name);
            // =============================================================

            // =============================================================
            // Async SPOTIFY WEB API SECTION

            String access_token = "BQBOS-1HWujvAgAAtTFTDdsvVwZnm54ppWP0LaniwXAIQ3Y1_LNxJM9lXYhry18Bcje_y-16V2fSig7qPFdYtTIrqsUCBJDcio8x5YJ68l5npM-vKAm_E0XQnBTWdLZ_ZDK1dy51OXhePooBL-rAfPtjl_iv08ihSPSnBG1aoJBLP21aTYWHGOZCjWat";
            String song_name = userRequest.getSongName();
            String artist_name = userRequest.getSongArtist();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> 
                asyncSpotify(access_token, song_name, artist_name, queue_id)
            );
            // =============================================================

            // Return userRequest
            return sq;
            
        // Host on Local Device
        } else {

            // Send Queue to New User on Connect
            if (userRequest.getSongName().equals("NULL")) {
                return sq;
            }

            // =================================================================
            // QUEUE MANAGER SECTION
            // Set Song ID
            String queue_id = String.valueOf(curQueueId);
            curQueueId += 1;

            // for now, the song_id will be "" until the async spotify returns w resources
            String song_id = "";

            // Add song to Queue
            Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), queue_id, song_id, userRequest.getUser());
            sd.add(newSong);
            sq.push(newSong);
            sq.printQueue();
            // =================================================================

            // =================================================================
            // < Old >  SPOTIFY WEB API SECTION
            // String access_token = "BQA3g0IRJgTO6LcN-rgrPd6bC-KEzzT3FaSAkQEmhIN6oGhunH_j-bT5iwvfR-0emeWjNgXkwrM8Xs0mb7G9_ix9gKn3jxGmr2VLIbYbAHY8Uh5TdHWrHTCRhuFR12CsWCSbsUByn0SyX9VTlXutJ_pJiWcOrQY1hrdD--40HGKa3EhtUZgCdlqu-qAu";
            // SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);
            // System.out.println("### Queuing Song ###");
            // String song_name = userRequest.getSongName();
            // String artist_name = userRequest.getSongArtist();
            // // Queue Song on Spotify Web API
            // P.queueSong(song_name, artist_name);
            // =================================================================

            // =================================================================
            // Async SPOTIFY WEB API SECTION
            String access_token = "dummy_token";
            String song_name = userRequest.getSongName();
            String artist_name = userRequest.getSongArtist();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> 
                asyncSpotify(access_token, song_name, artist_name, queue_id)
            );
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
            sd.like(id, 1);
        } else if (vote == DISLIKE) {
            // Dislike Song
            int numLikes = sd.dislike(id, 1);
            // Check if Song has been vetoed
            if (numLikes < 0) {
                Song song = sd.getSongById(id);
                sq.remove(song);
                sd.removeById(id);
                // Return vetoed Song ID
                return id;
            }
        } else if (vote == DISLIKE_TO_LIKE) {
            // Dislike to Like Song
            sd.like(id, 2);
        } else if (vote == LIKE_TO_DISLIKE) {
            // Dislike Song
            int numLikes = sd.dislike(id, 2);
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

    // Async SPOTIFY WEB API BLOCK
    // Circumvents Spotify Web API round-trip time
    public void asyncSpotify(String token, String song_name, String artist_name, String queue_id) {
        
        boolean result = false;
        String result_song_id = "";

        // Attempt to find a Song match and add to Spotify Queue
        try {

            SpotifyPlaybackController P = new SpotifyPlaybackController(token);
            System.out.println("### Queuing Song ###");

            // if !SONG_REC is appended to the song name it means we want a rec
            if (song_name.contains("!SONG_REC")) {

                String cleaned_name = song_name.replaceAll("!REC", "");
                TrackObject track = P.getSong(cleaned_name, artist_name);

                // now that we have the track, get the id, artist_id, and genre
                String song_id = track.getId();
                String artist_id = track.getFirstArtistId();

                System.out.println("### Generating Recommendation for: ###");
                System.out.println("# Song_ID = " + song_id + " #");
                System.out.println("# Artist_ID = " + artist_id + " #");

                /* send this to the second pi - serialize and send */
                MessageRequest rec_request = new MessageRequest(1, song_id, artist_id, null);
                String serialized_request = MessageRequestSerializer.serialize(rec_request);

                try (Socket socket = new Socket(HOSTNAME,PORT);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        // write the serialized request to the output
                        out.println(serialized_request);
                        System.out.println("Sent to server: " + serialized_request);

                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("(Would be queuing the returned song)");

                /**
                 * 
                 * TODO: need to listen for response from second pi
                 * 
                 */

                result_song_id = ""; // would set this to response from pi2

                System.out.println("### Queuing Song ###");

                result = P.queueSong(result_song_id);

            }
            else if (song_name.equals("!SESSION_REC"))  {

                System.out.println("### Displaying Session ###");

                // get the queue session
                List<Pair<String, Integer>> session = sq.getSession();

                for (Pair<String, Integer> p : session) {

                    System.out.println("Song ID: " + p.getValue0());
                    System.out.println("Likes: " + p.getValue1());

                }

                System.out.println("### Generating Session Recommendation ###");

                /* send this to the second pi - serialize and send */
                MessageRequest rec_request = new MessageRequest(2, "", "", session);
                String serialized_request = MessageRequestSerializer.serialize(rec_request);

                try (Socket socket = new Socket(HOSTNAME,PORT);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        // write the serialized request to the output
                        out.println(serialized_request);
                        System.out.println("Sent to server: " + serialized_request);

                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("(Would be queuing the returned song)");

                /**
                 * 
                 * TODO: need to listen for response from second pi
                 * 
                 */

                result_song_id = ""; // would set this to result from pi2

                System.out.println("### Queuing Song ###");

                result = P.queueSong(result_song_id);
                

            }
            // otherwise just queue the song as normal
            else    {

                System.out.println("### Queuing Song ###");

                result_song_id = P.queueSong(song_name, artist_name);
                result = true;
                
            }

        } catch (Exception e) {
            System.err.println("Async Spotify Queue Song FAILED");
        }

        // now we want to update the song dict to reflect the spotify resources
        if (result) {
            System.out.println("Async Spotify Queue Song SUCCESS");
            sd.updateSongId(queue_id, result_song_id);
            System.out.println("Updated SongDict: Queue_ID - " + queue_id + " with Song_ID - " + result_song_id);
        }
        // Return silently
        System.out.println("Async Spotify Queue Song FAILURE");

    }

}
