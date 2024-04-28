package com.mm.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.javatuples.Pair;

import com.mm.v3.MessageRequest;
import com.mm.v3.MessageResponse;
import com.mm.v2.communication.MessageRequestDeserializer;
import com.mm.v2.communication.MessageResponseSerializer;
import com.mm.v2.requests.RecommendationRequest;
import com.mm.v2.requests.RefreshAccessTokenRequest;
import com.mm.v2.responses.AccessTokenResponse;
import com.mm.v2.responses.RecommendationResponse;
import com.mm.v2.song.TrackObject;

public class RecommenderApp2 {

    private static String refresh_token = "AQA0oPc9DSdol2r5SxFx3LhHXGylRH4HIevFmjH1605DWojB5jam36ZnluQg34DksHFRv1yOoB0pGOsYRfBUXI1PIyoLeRdGa2TaUE14WUHjupZyE_c2gOvR6RQEMALI7nc";
    private static int MAX_AUTH_TIME_MS = 60000 * 30;

    public static void main(String[] args) {

        // access token for auth use
        String access_token = "";
        boolean first_authorization = true;
        // timing stuff
        long current_time_millis = 0;
        long last_auth_time = 0;

        // create the song attribute database
        SongAttributeDatabase db = new SongAttributeDatabase();

        int port = 5000; // The server will listen on this port

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            
            while (true) {

                // accept message from client
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    
                    // read the message from the first pi
                    String message = in.readLine();
                    System.out.println("Received from client: " + message);

                    // message handling: deserialize the string to message request
                    MessageRequest rec_request = MessageRequestDeserializer.deserialize(message);

                    // mark the current time
                    current_time_millis = System.currentTimeMillis();

                    // see if we need to update the access token
                    long time_diff = current_time_millis - last_auth_time;
                    /* if the timer has passed certain threshold, regen token */
                    if (first_authorization || time_diff >= MAX_AUTH_TIME_MS)   {
                        access_token = update_access_token();
                        last_auth_time = current_time_millis;
                        first_authorization = false;
                    }
                    
                    // now separate control flow based on message_id

                    TrackObject recommended_song = null;
                    int message_id = rec_request.getMessageId();

                    /*
                     * message_id == 1
                     * 
                     * this message is a song specific recommendation
                     * generate a recommendation using the following fields:
                     * - song_id
                     * - artist_id 
                     * 
                     */
                    if (message_id == 1)    {

                        String song_id = rec_request.getSongId();
                        String artist_id = rec_request.getArtistId();

                        recommended_song = getRecommendationFromSong(access_token, db, song_id, artist_id);
    
                        System.out.println("### Got Recommendation ###");
                        System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

                    }
                    /**
                     * message_id == 2
                     * 
                     * this message is a general session request
                     * generate a recommendation using the following fields:
                     * - session
                     * 
                     */
                    else if (message_id == 2)   {

                        List<Pair<String, Integer>> session = rec_request.getSession();

                        recommended_song = getRecommendationFromSession(access_token, db, session);

                        System.out.println("### Got Recommendation ###");
                        System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

                    }
                    else if (message_id == 3)    {

                        String song_id = rec_request.getSongId();
                        String artist_id = rec_request.getArtistId();

                        recommended_song = getRecommendationFromSongEndless(access_token, db, song_id, artist_id);
    
                        System.out.println("### Got Recommendation ###");
                        System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

                    }
                    else    {
                        System.out.println("Unknown message request type");
                    }

                    System.out.println("Preparing to send back to core");

                    String rec_song_id = recommended_song.getId();
                    String rec_song_name = recommended_song.getName();
                    String rec_artist_name = recommended_song.getArtistString();
                    int rec_song_duration = recommended_song.getDuration();

                    // now that we have the recommended song, build the message to send back
                    MessageResponse rec_response = new MessageResponse(rec_song_id, rec_song_name, rec_artist_name, rec_song_duration);

                    String serialized_response = MessageResponseSerializer.serialize(rec_response);

                    System.out.println("Serialized Response ** Sending Now");

                    out.println(serialized_response);
                    System.out.println("Sent to Core: " + serialized_response);

                             
                } catch (Exception e) {
                    System.out.println("Exception in client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static TrackObject getRecommendationFromSong(String access_token, SongAttributeDatabase db, String song_id, String artist_id) {

        System.out.println("### Getting Recommendation for song_id: " + song_id + " ###");

        String seed = SeedGenerator.generateSeed(access_token, db, song_id, artist_id);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_euclidean(access_token, db, song_id, rec.getTracks());

        return recommended_song;

    }

    public static TrackObject getRecommendationFromSongEndless(String access_token, SongAttributeDatabase db, String song_id, String artist_id) {

        System.out.println("### Getting Recommendation (endless) for song_id: " + song_id + " ###");

        String seed = SeedGenerator.generateSeed(access_token, db, song_id, artist_id);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_random(access_token, db, rec.getTracks());

        return recommended_song;

    }

    public static TrackObject getRecommendationFromSession(String access_token, SongAttributeDatabase db, List<Pair<String, Integer>> session) {

        System.out.println("### Getting Recommendation from session songs ###");

        cleanSession(session);

        String seed = SeedGenerator.generateSeed(access_token, db, session);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_random(access_token, db, rec.getTracks());

        return recommended_song;

    }

    public static void cleanSession(List<Pair<String, Integer>> session)    {

        for (int i = 0; i < session.size();)    {

            Pair<String, Integer> p = session.get(i);
            if (p.getValue0() == "")    { session.remove(i); }
            else    { i++; }

        }

    }

    public static String update_access_token() {
        
        AccessTokenResponse resp = new RefreshAccessTokenRequest().requestAccessToken(refresh_token);
        String access_token = resp.getAccessToken();

        System.out.println("Spotify Access Token: " + access_token);
        return access_token;

    }

    
}
