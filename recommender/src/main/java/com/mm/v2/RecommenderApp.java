package com.mm.v2;

import java.util.ArrayList;
import java.util.List;

import com.mm.v2.requests.RecommendationRequest;
import com.mm.v2.responses.RecommendationResponse;
import com.mm.v2.song.TrackObject;

import org.javatuples.Pair;

/**
 * Hello world!
 *
 */
public class RecommenderApp {

    public static void main( String[] args )    {

        String access_token = "BQDB57m5w_i8ahouyanFY3dMrAqdtB-KRp7LJ6eHWMuTUMKw32tmgEJsY_JS012JfhDN6EA9OfMIyPpVmFSf5rzTWsqflgwoc_Q7oWh87CJrYFknualF9P3lqtqjBWTm0n8JiNCK7a88Cw3ZXnYzxDLBlkBm7rH0JDIr4DrnpJSKuhYL1Zb11HAFU-Bd";
        
        SongAttributeDatabase db = new SongAttributeDatabase();

        /** will listen for incoming message requests from first pi */

        /**
         * 
         * in this case below, we are given the following in which to generate
         * a song similarity:
         * 
         * - song_id
         * - artist_id
         * - genre
         * 
         */


        boolean from_song = false;
        /** with this info we can do the following: */

        if (from_song)  {

            String song_id = "41ETKVJbZDSjATzW2wAqmc"; // would parse from req
            String artist_id = "3WrFJ7ztbogyGnTHbHJFl2"; // would parse from req
    
            TrackObject recommended_song = getRecommendationFromSong(access_token, db, song_id, artist_id);
    
            System.out.println("### Got Recommendation ###");
            System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

        }

        else    {

            /** test out the session recommendation here */
            Pair<String, Integer> p1 = new Pair<String,Integer>("1xzBco0xcoJEDXktl7Jxrr", 0);
            Pair<String, Integer> p2 = new Pair<String,Integer>("54X78diSLoUDI3joC2bjMz", 6);
            Pair<String, Integer> p3 = new Pair<String,Integer>("41ETKVJbZDSjATzW2wAqmc", 4);

            List<Pair<String, Integer>> session = new ArrayList<Pair<String, Integer>>();
            session.add(p1);
            session.add(p2);
            session.add(p3);

            TrackObject recommended_song = getRecommendationFromSession(access_token, db, session);

            System.out.println("### Got Recommendation ###");
            System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

        }

        /** now would send this back to first pi to be queued */
        
    }

    public static TrackObject getRecommendationFromSong(String access_token, SongAttributeDatabase db, String song_id, String artist_id) {

        System.out.println("### Getting Recommendation for song_id: " + song_id + " ###");

        String seed = SeedGenerator.generateSeed(access_token, db, song_id, artist_id);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_euclidean(access_token, db, song_id, rec.getTracks());

        return recommended_song;

    }

    public static TrackObject getRecommendationFromSession(String access_token, SongAttributeDatabase db, List<Pair<String, Integer>> session) {

        System.out.println("### Getting Recommendation from session songs ###");

        String seed = SeedGenerator.generateSeed(access_token, db, session);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_random(access_token, db, rec.getTracks());

        return recommended_song;

    }

}
