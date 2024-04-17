package com.mm.v1.queue;

import org.springframework.stereotype.Controller;
import com.google.gson.Gson;

import com.mm.v1.SpotifyPlaybackController;
import com.mm.v1.communication.MessageRequestSerializer;
import com.mm.v1.communication.MessageResponseDeserializer;
import com.mm.v1.requests.RefreshAccessTokenRequest;
import com.mm.v1.responses.AccessTokenResponse;
import com.mm.v1.song.TrackObject;
// import com.mm.v1.scheduler.SongQueueProcessor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mm.v3.MessageRequest;
import com.mm.v3.MessageResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.*;
import java.net.*;

import org.javatuples.Pair;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class QueueController {
    // Used to send queue in async operation
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // Vote Decisions
    private static final int NOTHING = 0;
    private static final int LIKE = 1;
    private static final int DISLIKE = 2;
    private static final int DISLIKE_TO_LIKE = 3;
    private static final int LIKE_TO_DISLIKE = 4;
    // Connection
    private static final int PORT = 5000;
    private static final String HOSTNAME = "172.26.54.24";
    // Song Queue
    private static UserDict ud = new UserDict();
    private static SongQueue sq = new SongQueue();
    private static SongDict sd = new SongDict();
    private static SongQueueProcessor ps = new SongQueueProcessor(sq, HOSTNAME, PORT);
    private static int curQueueId = 0;
    // Raspberry Pi
    // private static PiClient pi;
    private static boolean pi_active = true;
    // Authorization
    private static String refresh_token = "AQA0oPc9DSdol2r5SxFx3LhHXGylRH4HIevFmjH1605DWojB5jam36ZnluQg34DksHFRv1yOoB0pGOsYRfBUXI1PIyoLeRdGa2TaUE14WUHjupZyE_c2gOvR6RQEMALI7nc";

    // private static max_time
    private static int MAX_AUTH_TIME_MS = 60000 * 30;

    private String access_token;
    private boolean first_authorization;
    private boolean first_user = true;
    private boolean first_song = true;

    // timing stuff
    private long current_time_millis;
    private long last_auth_time;


    @MessageMapping("/queue.sendRequest")
    @SendTo("/topic/public")
    public String sendRequest(@Payload Request userRequest) {

        // mark the current time
        this.current_time_millis = System.currentTimeMillis();

        // Host on Raspberry Pi
        // Send Queue to New User on Connect
        if (userRequest.getMessageType() == MessageType.CONNECT) {

            if (this.first_user)    {
                System.out.println("Initializing queue controller: marking time");
                this.current_time_millis = System.currentTimeMillis();
                this.last_auth_time = this.current_time_millis;
                this.first_authorization = true;
                this.access_token = "";
                this.first_user = false;
            }

            // Add User to UserDict
            System.out.println("### Adding User ###   " + userRequest.getUserId());
            ud.addUser(userRequest.getUserId());
            Gson gson = new Gson();
            return gson.toJson(sq);
        }

        // =============================================================
        // QUEUE MANAGER SECTION

        // Set queue ID
        String queue_id = String.valueOf(curQueueId);
        curQueueId += 1;

        // for now, the song_id will be "" until the async spotify returns w resources
        String song_id = "";

        // Add song to Queue
        MessageType messageType = userRequest.getType();
        boolean isRec = messageType == MessageType.SONGREC || messageType == MessageType.SESSIONREC;
        Song newSong = new Song(userRequest.getSongName(), userRequest.getSongArtist(), queue_id, song_id,
                userRequest.getUser(), userRequest.getUserId(), isRec);
        sd.add(newSong);
        ud.addSong(userRequest.getUserId(), newSong);
        sq.push(newSong);
        sq.printQueue();
        ud.printDict();

        // =============================================================

            // =============================================================
            // Async SPOTIFY WEB API SECTION

        long time_diff = this.current_time_millis - this.last_auth_time;
        /* if the timer has passed certain threshold, regen token */
        if (first_authorization || time_diff >= MAX_AUTH_TIME_MS)   {
            this.update_access_token();
            this.last_auth_time = this.current_time_millis;
            this.first_authorization = false;
        }
        // =============================================================
        // Async SPOTIFY WEB API SECTION

    
        String song_name = userRequest.getSongName();
        String artist_name = userRequest.getSongArtist();
        CompletableFuture<Void> future = CompletableFuture
                .runAsync(() -> asyncSpotify(this.access_token, song_name, artist_name, queue_id, messageType))
                .thenAccept(result -> {
                    Gson gson = new Gson();
                    String updatedQueue = gson.toJson(sq);
                    // SEND updated queue
                    if (messageType != MessageType.REQUEST) {
                        System.out.println("FINISHED ASYNC FUNCTION");
                        newSong.setRecComplete();
                        gson = new Gson();
                        updatedQueue = gson.toJson(sq);
                        messagingTemplate.convertAndSend("/topic/public", updatedQueue);
                    }
                });
        
        // =============================================================

        // Want like buttons to show
        if (messageType == MessageType.REQUEST) {
            newSong.setRecComplete();
        }
        Gson gson = new Gson();
        return gson.toJson(sq);

        // Host on Local Device

    }

    @MessageMapping("/queue.addUser")
    @SendTo("/topic/public")
    public Request addUser(
            @Payload Request userRequest,
            SimpMessageHeaderAccessor accessor) {
        // Accept a new user
        accessor.getSessionAttributes().put("username", userRequest.getUser());
        return userRequest;
    }

    //UNCOMMENT THIS
    @MessageMapping("/userInactive")
    private void removeInactiveLikes(@Payload UserId userId) {
        String userId2 = userId.getUserId();
        System.out.println("User " + userId2 + " has been inactive for 15 minutes");
        // Remove inactive likes
        ConcurrentLinkedDeque<Song> userSongs = ud.getUserSongs(userId2);
        //System.out.println("here are the user songs: " + ud.getUserSongs(userId2).size());
        for (Song song : userSongs) {
            if (song.getColor(userId2) == "green") {
                //song.setColor("none", userId2);
                int numLikes = sd.dislike(song.getQueueId(), 1);
                if (numLikes < 0) {
                    String queueId = song.getQueueId();
                    sq.remove(song);
                    sd.removeById(queueId);
                    ud.removeSong(userId2, song);
                    messagingTemplate.convertAndSend("/topic/remove", queueId);
                }
            } else if (song.getColor(userId2) == "red") {
                //song.setColor("none", userId2);
                sd.like(song.getQueueId(), 1);
            }
        }
    }

        //UNCOMMENT THIS
        @MessageMapping("/userActive")
        private void addInactiveLikes(@Payload UserId userId) {
        String userId2 = userId.getUserId();
        System.out.println("User " + userId2 + " is reactivated");
        // Remove inactive likes
        for (Song song : ud.getUserSongs(userId2)) {
            String queueId = song.getQueueId();
            String color = song.getColor(userId2);
            if (color == "red") {
                //song.setColor("none", userId2);
                int numLikes = sd.dislike(queueId, 1);
                if (numLikes < 0) {
                    sq.remove(song);
                    sd.removeById(queueId);
                    ud.removeSong(userId2, song);
                    messagingTemplate.convertAndSend("/topic/remove", queueId);
                }
            } else if(color == "green") {
                //song.setColor("none", userId2);
                sd.like(queueId, 1);
            }
        }
    }



    @MessageMapping("/queue.sendLike")
    @SendTo("/topic/remove")
    public String sendLike(
            @Payload Vote userVote) {
        // Extract Vote details
        String queueId = userVote.getQueueId();
        int vote = userVote.getVote();
        String userId = userVote.getUserId();
        String color = userVote.getColor();

        // Set color
        Song song = sd.getSongByQueueId(queueId);
        ud.addSong(userId, song);
        System.out.println("added song: " + song.getSongName() + " to user: " + userId);
        song.setColor(color, userId);
        // Process Vote
        if (vote == LIKE) {
            // Like Song
            sd.like(queueId, 1);
        } else if (vote == DISLIKE) {
            // Dislike Song
            int numLikes = sd.dislike(queueId, 1);
            // Check if Song has been vetoed
            if (numLikes < 0) {
                sq.remove(song);
                sd.removeById(queueId);
                ud.removeSong(userId, song);
                // Return vetoed Song ID
                return queueId;
            }
        } else if (vote == DISLIKE_TO_LIKE) {
            // Dislike to Like Song
            sd.like(queueId, 2);
        } else if (vote == LIKE_TO_DISLIKE) {
            // Dislike Song
            int numLikes = sd.dislike(queueId, 2);
            // Check if Song has been vetoed
            if (numLikes < 0) {
                sq.remove(song);
                sd.removeById(queueId);
                ud.removeSong(userId, song);
                // Return vetoed Song ID
                return queueId;
            }
        }

        // Return something
        return "none";
    }


    // public void asyncSpotifyTest(String token, String song_name, String artist_name, String queue_id,
    //         MessageType messageType) {
    //     if (MessageType.REQUEST == messageType) {
    //         return;
    //     }

    //     try {
    //             Thread.sleep(5000);
    //     String result_song_id = "1";
    //     sd.updateSong(queue_id, result_song_id, "Test Song Name", "Test Artist Name");
    //     } catch (InterruptedException e) {
    //         e.printStackTrace();
    //     }
    // }

    // Async SPOTIFY WEB API BLOCK
    // Circumvents Spotify Web API round-trip time
    public void asyncSpotify(String token, String song_name, String artist_name, String queue_id,
            MessageType messageType) {

        boolean result = false;
        String result_song_id = "";
        MessageResponse rec_response = null;

        // Attempt to find a Song match and add to Spotify Queue
        try {

            SpotifyPlaybackController P = new SpotifyPlaybackController(token);

            // if !SONG_REC is appended to the song name it means we want a rec
            if (messageType == MessageType.SONGREC) {

                // String cleaned_name = song_name.replaceAll(" !SONG_REC", "");
                TrackObject track = P.getSong(song_name, artist_name);

                // now that we have the track, get the id, artist_id, and genre
                String song_id = track.getId();
                String artist_id = track.getFirstArtistId();

                System.out.println("### Generating Recommendation for: ###");
                System.out.println("# Song_ID = " + song_id + " #");
                System.out.println("# Artist_ID = " + artist_id + " #");

                /* send this to the second pi - serialize and send */
                MessageRequest rec_request = new MessageRequest(1, song_id, artist_id, null);
                String serialized_request = MessageRequestSerializer.serialize(rec_request);

                try (Socket socket = new Socket(HOSTNAME, PORT);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // write the serialized request to the output
                    out.println(serialized_request);
                    System.out.println("Sent to server: " + serialized_request);

                    System.out.println("Awaiting response from recommender");

                    String response = in.readLine();
                    System.out.println("Recevied from server: " + response);

                    // now deserialize the response
                    rec_response = MessageResponseDeserializer.deserialize(response);

                    System.out.println("Deserialized from server!");

                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                result_song_id = rec_response.getSongId(); // would set this to response from pi2

                System.out.println("### Updating Song Queue ###");

                sd.updateSong(queue_id, result_song_id, rec_response.getSongName(), rec_response.getArtistName(), rec_response.getDuration());

                System.out.println("Updated SongDict: Queue_ID - " + queue_id + " with Song_ID - " + result_song_id);

                if (this.first_song)    {
                    System.out.println("FIRST SONG: Queueing, and starting Scheduler");
                    result = P.queueSong(result_song_id);
                    this.first_song = false;
                    // and so now also initiate the queue scheduler
                    ps.processNextSong();
                }

            } else if (messageType == MessageType.SESSIONREC) {

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

                try (Socket socket = new Socket(HOSTNAME, PORT);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // write the serialized request to the output
                    out.println(serialized_request);
                    System.out.println("Sent to server: " + serialized_request);

                    System.out.println("Awaiting response from recommender");

                    String response = in.readLine();
                    System.out.println("Recevied from server: " + response);

                    // now deserialize the response
                    rec_response = MessageResponseDeserializer.deserialize(response);

                    System.out.println("Deserialized from server!");

                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                result_song_id = rec_response.getSongId(); // would set this to response from pi2

                System.out.println("### Updating Song Queue ###");

                sd.updateSong(queue_id, result_song_id, rec_response.getSongName(), rec_response.getArtistName(), rec_response.getDuration());

                System.out.println("Updated SongDict: Queue_ID - " + queue_id + " with Song_ID - " + result_song_id);

                if (this.first_song)    {
                    System.out.println("FIRST SONG: Queueing, and starting Scheduler");
                    result = P.queueSong(result_song_id);
                    this.first_song = false;
                    // and so now also initiate the queue scheduler
                    ps.processNextSong();
                }

            }
            // otherwise just queue the song as normal
            else {

                System.out.println("### Updating Song Queue ###");

                TrackObject result_song = P.getSong(song_name, artist_name);

                /** TODO: if the result song wasn't found, remove from queue */

                result_song_id = result_song.getId();
                int result_song_duration = result_song.getDuration();
                result = true;

                sd.updateSongId(queue_id, result_song_id, result_song_duration);
                System.out.println("Updated SongDict: Queue_ID - " + queue_id + " with Song_ID - " + result_song_id);

                if (this.first_song)    {
                    System.out.println("FIRST SONG: Queueing, and starting Scheduler");
                    result = P.queueSong(result_song_id);
                    this.first_song = false;
                    // and so now also initiate the queue scheduler
                    ps.processNextSong();
                }

            }

        } catch (Exception e) {
            System.err.println("Async Spotify Queue Song FAILED");
        }

        // now we want to update the song dict to reflect the spotify resources
        if (result) {
            System.out.println("Async Spotify Queue Song SUCCESS");
        } else {
            System.out.println("Async Spotify Queue Song FAILURE");
        }

    }

    public void update_access_token() {
        
        AccessTokenResponse resp = new RefreshAccessTokenRequest().requestAccessToken(refresh_token);
        String access_token = resp.getAccessToken();

        System.out.println("Spotify Access Token: " + access_token);
        this.access_token = access_token;
        ps.setAccessToken(access_token);

    }

    /** nested song queue processing class - handles scheduling */

    public static class SongQueueProcessor {

        private SongQueue sq;
        private String hostname;
        private int port;
        private ScheduledExecutorService executor;
        private String access_token;
        private boolean first_song;
        private MessageResponse prefetched = null;
        
        private int buffer = 5000;
    
        public SongQueueProcessor(SongQueue sq, String hostname, int port) {
            this.sq = sq;
            this.hostname = hostname;
            this.port = port;
            // can be single threaded to start
            this.executor = Executors.newSingleThreadScheduledExecutor();
            this.access_token = "";
            this.first_song = true;
        }
    
        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }
    
        public void startProcessing()   {
            this.processNextSong();
        }
    
        public void processNextSong()   {
    
            System.out.println("__SCHEDULER__: processing next song");

            SpotifyPlaybackController P = new SpotifyPlaybackController(this.access_token);

            // get the duration of the currently playing song
            long duration;
            if (this.first_song)    {
                System.out.println("__SCHEDULER__: first song, adding some buffer time");
                duration = getCurrentSongDuration() - buffer;
            }
            else    {
                duration = getCurrentSongDuration();
            }
            this.first_song = false;

            // before scheduling, if the queue has no next up songs, prefetch a req
            if (needRecommendation())   {

                System.out.println("__SCHEDULER__: prefetching rec");
                Song curr_song = this.sq.peek();
                prefetched = getEndlessQueueRecommendation(P, curr_song.getSongName(), curr_song.getSongArtist());
            }

    
            executor.schedule( () -> {
    
                System.out.println("__SCHEDULER__: timer reached, queueing song now");

                /** ------- BEGIN CRITICAL AREA ------- */

                // get the next song in the queue (the one we should queue)
                String song_id = getNextSong(P, prefetched);
                // then actually queue this song 
                boolean result = P.queueSong(song_id);
    
                sq.pop();

                /** ------- END CRITICAL AREA ------- */
    
                // process the next song recursively
                processNextSong();
    
            }, duration, TimeUnit.MILLISECONDS);
    
        }

        public boolean needRecommendation() {

            Song next_song = this.sq.peekSecondElement();
            if (next_song == null)  {

                System.out.println("__SCHEDULER__: queue empty, need to get rec");
                return true;

            }
            return false;

        }
    
        public String getNextSong(SpotifyPlaybackController P, MessageResponse prefetched) {
    
            // peek at the next song in the queue
            Song next_song = this.sq.peekSecondElement();
            // if there are no songs next up in the queue, generate a dj rec from the prev
            if (next_song == null)  {
                System.out.println("__SCHEDULER__: queue still empty, adding prefetched rec to queue");
                return queueEndlessRecommendation(prefetched);
    
            }
            return next_song.getSongId();
    
        } 
    
        public long getCurrentSongDuration()    {
    
            Song curr_song = this.sq.peek();
            if (curr_song == null)  {
                System.out.println("__SCHEDULER__: failed to get curr song duration from queue");
            }
            return curr_song.getDuration();
    
        }
    
        public MessageResponse getEndlessQueueRecommendation(SpotifyPlaybackController P, String song_name, String artist_name) {
    
            TrackObject track = P.getSong(song_name, artist_name);
            MessageResponse rec_response = null;
    
            // now that we have the track, get the id, artist_id, and genre
            String song_id = track.getId();
            String artist_id = track.getFirstArtistId();
    
            System.out.println("### Generating Endless Queue Rec for: ###");
            System.out.println("# Song_ID = " + song_id + " #");
            System.out.println("# Artist_ID = " + artist_id + " #");
    
            /* send this to the second pi - serialize and send */
            MessageRequest rec_request = new MessageRequest(1, song_id, artist_id, null);
            String serialized_request = "";
            try {
                serialized_request = MessageRequestSerializer.serialize(rec_request);
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            try (Socket socket = new Socket(hostname, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
                // write the serialized request to the output
                out.println(serialized_request);
                System.out.println("Sent to server: " + serialized_request);
    
                System.out.println("Awaiting response from recommender");
    
                String response = in.readLine();
                System.out.println("Recevied from server: " + response);
    
                // now deserialize the response
                rec_response = MessageResponseDeserializer.deserialize(response);
    
                System.out.println("Deserialized from server!");
    
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
    
            return rec_response;
        }

        public String queueEndlessRecommendation(MessageResponse rec_response)  {

            String result_song_id = rec_response.getSongId(); // would set this to response from pi2
    
            System.out.println("### Adding to Song Queue ###");

            String queue_id = String.valueOf(curQueueId);
            curQueueId += 1;
            String username = "MM";
            String user_id = "18500";
            boolean isRec = true;
    
            Song newSong = new Song(rec_response.getSongName(), rec_response.getArtistName(), queue_id, result_song_id,
            username, user_id, isRec);
            newSong.setRecComplete();

            sd.add(newSong);
            sq.push(newSong);
    
            System.out.println("Added to SongDict: Queue_ID - " + queue_id + " with Song_ID - " + result_song_id);

            return result_song_id;

        }
        
    }
    

}
