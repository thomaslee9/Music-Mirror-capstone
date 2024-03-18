package com.mm.v2;

import java.util.Random;

import com.mm.v2.song.TrackObject;

public class RecommendationRanker {

    public static TrackObject rank(TrackObject[] track_objects) {

        System.out.println("### Ranking Recommendations ###");

        for (TrackObject track : track_objects) {
            System.out.println(track.getName());
        }

        Random rand = new Random();
        // Generate random integers in range 0 to 999
        int idx = rand.nextInt(track_objects.length);

        return track_objects[idx];

    }
    
}
