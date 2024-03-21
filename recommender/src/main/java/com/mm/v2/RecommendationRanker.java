package com.mm.v2;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.mm.v2.requests.TrackAudioFeaturesRequest;
import com.mm.v2.song.SongAudioFeatures;
import com.mm.v2.song.TrackObject;
import org.javatuples.Pair;

public class RecommendationRanker {

    private static String[] base_features = new String[] { "acousticness", "danceability", "energy",
                                                           "instrumentalness", "liveness", "loudness", 
                                                           "speechiness", "tempo", "valence" };

    public static TrackObject rank_random(String access_token, SongAttributeDatabase db, TrackObject[] track_objects) {

        System.out.println("### Ranking Recommendations - Using Random ###");

        for (TrackObject track : track_objects) {
            System.out.println(track.getName());
        }

        Random rand = new Random();
        int idx = rand.nextInt(track_objects.length);

        return track_objects[idx];

    }

    public static TrackObject rank_euclidean(String access_token, SongAttributeDatabase db, String song_id, TrackObject[] track_objects) {

        System.out.println("### Ranking Recommendations - Using L2 Norm ###");

        SongAudioFeatures original_features = db.GetAudioFeatures(song_id);
        List<SongAudioFeatures> recommendations_features = getRecommendationsAttributes(access_token, db, track_objects);

        // *** this is where we have fun - now we can compute similarity ***

        // get the min max bounds
        Map<String, Pair<Double, Double>> minmax = getMinMaxBounds(original_features, recommendations_features);

        int min_idx = recommendations_features.size();
        Double min_distance = 10000000000000.0;

        // now iterate thru and find the min distance index
        for (int i = 0; i < recommendations_features.size(); i++)   {

            SongAudioFeatures to_compare = recommendations_features.get(i);
            Double distance = computeEuclideanDistance(minmax, original_features, to_compare);

            System.out.println("* Computed L2 Distance: " + distance);

            if (distance < min_distance)    {
                // update the min and set the corresponding index
                min_distance = distance;
                min_idx = i;
            }
        }

        // now we can finally return the proper track object
        TrackObject result = track_objects[min_idx];

        for (TrackObject t : track_objects) {
            System.out.println(t.getName() + " ------ " + t.getArtistString());
        }

        return result;

    }


    private static List<SongAudioFeatures> getRecommendationsAttributes(String access_token, SongAttributeDatabase db, TrackObject[] track_objects)    {

        List<SongAudioFeatures> recommendation_features = new ArrayList<SongAudioFeatures>();

        for (TrackObject track : track_objects) {

            String song_id = track.getId();
            SongAudioFeatures features = db.GetAudioFeatures(song_id);
            // if it wasn't in the db then get from spotify and add to db
            if (features == null)   {
                // make the request and add to db
                features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, song_id);
                db.AddSong(song_id, features);
            }
            recommendation_features.add(features);

        }
        return recommendation_features;
    }

    private static Map<String, Pair<Double, Double>> getMinMaxBounds(SongAudioFeatures song_features, List<SongAudioFeatures> recommendation_features)  {

        // temporarily add the original song
        recommendation_features.add(song_features);

        Map<String, Pair<Double, Double>> result = new HashMap<String, Pair<Double, Double>>();

        // for each of the features, get the min and max
        for (String feature : base_features) {

            Double min = 10000000000.0;
            Double max = 0.0;
            // now actually iterate thru the songs
            for (SongAudioFeatures f : recommendation_features) {

                Double feature_value = getFeatureValue(feature, f);
                // update values
                min = Math.min(min, feature_value);
                max = Math.max(max, feature_value);

            }
            // now add this to the min max map
            Pair<Double, Double> minmax = new Pair<Double,Double>(min, max);
            result.put(feature, minmax);

        }
        // remove the original from rec feature list before returning
        recommendation_features.remove(recommendation_features.size()-1);
        return result;

    }

    private static Double getFeatureValue(String f, SongAudioFeatures features)   {

        Double result = 0.0;

        if      (f.equals("acousticness"))   {
            result = Double.parseDouble(features.getAcousticness());
        }
        else if (f.equals("danceability"))  {
            result = Double.parseDouble(features.getDanceability());
        }
        else if (f.equals("energy"))    {
            result = Double.parseDouble(features.getEnergy());
        }
        else if (f.equals("instrumentalness"))  {
            result = Double.parseDouble(features.getInstrumentalness());
        }
        else if (f.equals("liveness"))  {
            result = Double.parseDouble(features.getLiveness());
        }
        else if (f.equals("loudness"))  {
            result = Double.parseDouble(features.getLoudness());
        }
        else if (f.equals("speechiness"))  {
            result = Double.parseDouble(features.getSpeechiness());
        }
        else if (f.equals("tempo"))  {
            result = Double.parseDouble(features.getTempo());
        }
        else if (f.equals("valence"))  {
            result = Double.parseDouble(features.getValence());            
        }

        return result;

    }

    /**
     * 
     * which features do we think are meaningful?
     * 
     * - acousticness
     * - danceability
     * - energy
     * - instrumentalness
     * - liveness
     * - loudness
     * - speechiness
     * - tempo
     * - valence
     * 
     */
    private static Double computeEuclideanDistance(Map<String, Pair<Double, Double>> minmax, 
                                                   SongAudioFeatures input, SongAudioFeatures output)   {

        Double sumofsquared = 0.0;

        for (String f : base_features) {

            // get the feature's min and max
            Pair<Double, Double> mm = minmax.get(f);
            Double f_min = mm.getValue0();
            Double f_max = mm.getValue1();
            Double diff = f_max - f_min;

            // get the input song's feature
            Double input_feature = getFeatureValue(f, input);
            // get the output song's feature
            Double output_feature = getFeatureValue(f, output);

            // scale the values (min-max normalization)
            Double scaled_input_feature = (input_feature - f_min) / diff;
            Double scaled_output_feature = (output_feature - f_min) / diff;

            // now add the sum of this diff squared
            Double difference = scaled_input_feature - scaled_output_feature;
            sumofsquared += (difference * difference);

        }

        return Math.sqrt(sumofsquared);
    }
    
}
