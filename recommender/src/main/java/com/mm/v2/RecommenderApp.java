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

        String access_token = "BQBTChnMcr5aYjlG8gRzq_mmKx8Im48wluo8W81RVAKI91Ux08J4Sm0Or2BnFdu4Z9KwZVHfsVRoh8hB3Bixw6a4NQpXX0mG5BPG20x8Z4lfi79qffZfL5L5YlW_JF88WswZTRxjY2bmVqC37PTjxZB9nS8no3s6fAfmWMNLzePJ_MVPrXj8YUZ_ltpC";

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
        TrackObject recommended_song = RecommendationRanker.rank(rec.getTracks());

        return recommended_song;

    }

}
