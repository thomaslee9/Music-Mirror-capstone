package com.mm.v2;

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

        String access_token = "BQDa4sCw9_CwiYrvvM3YP5t5zipHYvm6592PtAq_xGOsJUD41D50hksRPxGcR7wfua5-MsU-d1DU8GvTIj0Xss7mSHkfsUhqrsReTT-mWIaXF6kpU8f-s3y510xvDKTuPCYEnQS-UYunxTj1h_wuSQ4AROT2dcTwy5YwnRIKBM22fFk-wH0LS1bWkhk9";
        
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
            String[] genres = new String[0]; // would parse from req
    
            TrackObject recommended_song = getRecommendationFromSong(access_token, db, song_id, artist_id, genres);
    
            System.out.println("### Got Recommendation ###");
            System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());

        }

        else    {

            /** test out the session recommendation here */

        }

        /** now would send this back to first pi to be queued */
        
    }

    public static TrackObject getRecommendationFromSong(String access_token, SongAttributeDatabase db, String song_id, String artist_id, String[] genres) {

        System.out.println("### Getting Recommendation for song_id: " + song_id + " ###");

        String seed = SeedGenerator.generateSeed(access_token, db, song_id, genres, artist_id);
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
