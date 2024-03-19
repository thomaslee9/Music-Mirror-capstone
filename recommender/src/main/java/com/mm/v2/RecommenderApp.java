package com.mm.v2;

import com.mm.v2.requests.RecommendationRequest;
import com.mm.v2.responses.RecommendationResponse;
import com.mm.v2.song.TrackObject;

/**
 * Hello world!
 *
 */
public class RecommenderApp {

    public static void main( String[] args )    {

        /** will listen for incoming requests */

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

        String access_token = "BQDa4sCw9_CwiYrvvM3YP5t5zipHYvm6592PtAq_xGOsJUD41D50hksRPxGcR7wfua5-MsU-d1DU8GvTIj0Xss7mSHkfsUhqrsReTT-mWIaXF6kpU8f-s3y510xvDKTuPCYEnQS-UYunxTj1h_wuSQ4AROT2dcTwy5YwnRIKBM22fFk-wH0LS1bWkhk9";

        SongAttributeDatabase db = new SongAttributeDatabase();

        String song_id = "41ETKVJbZDSjATzW2wAqmc";
        String artist_id = "3WrFJ7ztbogyGnTHbHJFl2";
        String[] genres = new String[0];

        TrackObject recommended_song = getRecommendationFromSong(access_token, db, song_id, artist_id, genres);

        System.out.println("### Got Recommendation ###");
        System.out.println(recommended_song.getName() + " by " + recommended_song.getArtistString());
        
    }

    public static TrackObject getRecommendationFromSong(String access_token, SongAttributeDatabase db, String song_id, String artist_id, String[] genres) {

        System.out.println("### Getting Recommendation for song_id: " + song_id + " ###");

        String seed = SeedGenerator.generateSeed(access_token, db, song_id, genres, artist_id);
        System.out.println(seed);

        RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);
        TrackObject recommended_song = RecommendationRanker.rank_euclidean(access_token, db, song_id, rec.getTracks());

        return recommended_song;

    }

}
