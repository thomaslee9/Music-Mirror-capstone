package com.mm.v1.responses;

import com.mm.v1.recommendation.RecommendationSeedObject;
import com.mm.v1.song.TrackObject;

public class RecommendationResponse {

    private RecommendationSeedObject[] seeds;
    private TrackObject[] tracks;

    public TrackObject[] getTracks()    {
        return this.tracks;
    }
    
}
