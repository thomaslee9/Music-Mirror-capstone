package com.mm.v2;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.mm.v2.requests.GetTrackRequest;
import com.mm.v2.requests.TrackAudioFeaturesRequest;
import com.mm.v2.song.SongAudioFeatures;
import com.mm.v2.song.TrackObject;

public class SeedGenerator {

    public static String generateSeed(String access_token, SongAttributeDatabase db, String song_id, String artist_id)   {

        SeedBuilder builder = new SeedBuilder(artist_id, song_id);

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

    public static String generateSeed(String access_token, SongAttributeDatabase db, List<Pair<String, Integer>> session)  {

        int num_songs = 3;
        // from the map of song_ids to session likes, get the top 3 liked songs
        List<Pair<String, Integer>> topsongs = getMostLiked(num_songs, session);

        String[] song_ids = new String[num_songs];
        String[] artist_ids = new String[num_songs-1];

        int idx = 0;
        // now for each top song we need to get the info about it
        for (Pair<String, Integer> pair : topsongs) {

            String song_id = pair.getValue0();
            int likes = pair.getValue1();

            // get the track object and the features
            TrackObject track = new GetTrackRequest().getTrack(access_token, song_id);
            SongAudioFeatures features = db.GetAudioFeatures(song_id);
            // if it wasn't in the db then get from spotify and add to db
            if (features == null)   {
                // make the request and add to db
                features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, song_id);
                db.AddSong(song_id, features);
            }

            // collect the song_ids and artist_ids to pass into the builder
            song_ids[idx] = track.getId();
            if (idx != num_songs - 1)   { artist_ids[idx] = track.getFirstArtistId();   }

            idx += 1;
        }

        // now we get the builder and generate the seed
        SeedBuilder builder = new SeedBuilder(artist_ids, song_ids);

        /** TODO: aggregate the features here to pass into builder */
        builder.addMinPopularity("50");

        return builder.getSeed();

    }

    public static List<Pair<String, Integer>> getMostLiked(int num_songs, List<Pair<String, Integer>> session)   {

        // sort the session list in descending order
        Collections.sort(session, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
                return Integer.compare(p2.getValue1(), p1.getValue1());
            }
        });
        // now get the top three elements
        List<Pair<String, Integer>> top = session.subList(0, Math.min(session.size(), num_songs));

        return top;

    }
    
}
