package com.mm.v2.responses;

import com.mm.v2.recommendation.RecommendationSeedObject;
import com.mm.v2.song.TrackObject;

public class RecommendationResponse {

    private RecommendationSeedObject[] seeds;
    private TrackObject[] tracks;

    public TrackObject[] getTracks()    {
        return this.tracks;
    }
    
}
