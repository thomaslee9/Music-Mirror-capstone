package com.mm.v2;

import com.mm.v2.requests.TrackAudioFeaturesRequest;
import com.mm.v2.song.SongAudioFeatures;

public class SeedGenerator {

    public static String generateSeed(String access_token, SongAttributeDatabase db, String song_id, String[] genres, String artist_id)   {

        SeedBuilder builder = new SeedBuilder(artist_id, genres, song_id);

        // once we have the builder, now try and get song attributes
        SongAudioFeatures features = db.GetAudioFeatures(song_id);

        // if it wasn't in the db then get from spotify and add to db
        if (features == null)   {
            // make the request and add to db
            features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, song_id);
            db.AddSong(song_id, features);
        }

        // now we can use the retrieved features to construct a seed

        builder.addTargetTempo(features.getTempo());
        builder.addTargetAcousticness(features.getAcousticness());
        builder.addTargetInstrumentalness(features.getInstrumentalness());
        builder.addTargetEnergy(features.getEnergy());
        builder.addMinPopularity("50");

        return builder.getSeed();

    }
    
}
