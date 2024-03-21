package com.mm.v2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SeedBuilder {
    
    /**
     * Seed for generating song recommendations using Spotify's ML model
     * 
     * Supports use of the following song attributes (required = ***):
     * 
     * - track ***
     * - artist ***
     * - genre ***
     * - acousticness
     * - danceability
     * - energy
     * - instrumentalness
     * - key
     * - liveness
     * - loudness
     * - mode
     * - popularity
     * - speechiness
     * - tempo
     * - valence
     * 
     */
    private String seed = "";

    private static String[] default_genres = new String[] {"alternative", "rock", "country", "hip-hop", "r-n-b"};

    // String seed = "seed_artists=3WrFJ7ztbogyGnTHbHJFl2&seed_genres=rock&seed_tracks=2hOC9qItvmSkgMnxRjgPSr";

    public SeedBuilder(String artist_id, String track_id) {

        /**
        seed_artists=3WrFJ7ztbogyGnTHbHJFl2&seed_genres=alternative%2Crock%2Ccountry%2Chip-hop%2Cr-n-b&seed_tracks=41ETKVJbZDSjATzW2wAqmc 
        seed_artists=3WrFJ7ztbogyGnTHbHJFl2&seed_genres%3Dalternative%2Crock%2Ccountry%2Chip-hop%2Cr-n-b&seed_tracks=41ETKVJbZDSjATzW2wAqmc
         */

        this.seed = "seed_artists=" + artist_id + "&" +
                    "seed_tracks=" + track_id;

    }

    /**
     * @param artist_ids max of 5
     * @param genres max of 5
     * @param track_ids max of 5
     */
    public SeedBuilder(String[] artist_ids, String[] track_ids) {

        String seed_artists = "";
        String seed_tracks = "";

        for (int i = 0; i < artist_ids.length; i++) {
            if (i == 0) { seed_artists += artist_ids[i]; }
            else    { seed_artists += "," + artist_ids[i]; }
        }
        for (int i = 0; i < track_ids.length; i++) {
            if (i == 0) { seed_tracks += track_ids[i]; }
            else    { seed_tracks += "," + track_ids[i]; }
        }

        try {
            this.seed = "seed_artists=" + URLEncoder.encode(seed_artists, "UTF-8") + "&" 
                      + "seed_tracks=" + URLEncoder.encode(seed_tracks, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public String getSeed() {
        return this.seed;
    }

    /** ACCOUSTICNESS * Range: 0-1 */

    public void addMinAcousticness(String min_acousticness) {

        this.seed += "&min_acousticness=" + min_acousticness;

    }

    public void addMaxAcousticness(String max_acousticness) {

        this.seed += "&max_acousticness=" + max_acousticness;
        
    }

    public void addTargetAcousticness(String target_acousticness) {

        this.seed += "&target_acousticness=" + target_acousticness;
        
    }

    /** DANCEABILITY * Range: 0-1 */

    public void addMinDanceability(String min_danceability) {

        this.seed += "&min_danceability=" + min_danceability;

    }

    public void addMaxDanceability(String max_danceability) {

        this.seed += "&max_danceability=" + max_danceability;
        
    }

    public void addTargetDanceability(String target_danceability) {

        this.seed += "&target_danceability=" + target_danceability;
        
    }

    /** ENERGY * Range: 0-1 */

    public void addMinEnergy(String min_energy) {

        this.seed += "&min_energy=" + min_energy;

    }

    public void addMaxEnergy(String max_energy) {

        this.seed += "&max_energy=" + max_energy;
        
    }

    public void addTargetEnergy(String target_energy) {

        this.seed += "&target_energy=" + target_energy;
        
    }

    /** INSTRUMENTALNESS * Range: 0-1 */

    public void addMinInstrumentalness(String min_instrumentalness) {

        this.seed += "&min_instrumentalness=" + min_instrumentalness;

    }

    public void addMaxInstrumentalness(String max_instrumentalness) {

        this.seed += "&max_instrumentalness=" + max_instrumentalness;
        
    }

    public void addTargetInstrumentalness(String target_instrumentalness) {

        this.seed += "&target_instrumentalness=" + target_instrumentalness;
        
    }

    /** KEY * Range: 0-11 */

    public void addMinKey(String min_key)   {

        this.seed += "&min_key=" + min_key;

    }

    public void addMaxKey(String max_key)   {

        this.seed += "&max_key=" + max_key;
        
    }

    public void addTargetKey(String target_key)   {

        this.seed += "&target_key=" + target_key;
        
    }

    /** LIVENESS * Range: 0-1 */

    public void addMinLiveness(String min_liveness)   {

        this.seed += "&min_liveness=" + min_liveness;

    }

    public void addMaxLiveness(String max_liveness)   {

        this.seed += "&max_liveness=" + max_liveness;
        
    }

    public void addTargetLiveness(String target_liveness)   {

        this.seed += "&target_liveness=" + target_liveness;
        
    }

    /** LOUDNESS */

    public void addMinLoudness(String min_loudness) {

        this.seed += "&min_loudness=" + min_loudness;

    }

    public void addMaxLoudness(String max_loudness) {
        
        this.seed += "&max_loudness=" + max_loudness;

    }

    public void addTargetLoudness(String target_loudness) {

        this.seed += "&target_loudness=" + target_loudness;
        
    }

    /** MODE * Range: 0-1 */

    public void addMinMode(String min_mode) {

        this.seed += "&min_mode=" + min_mode;
        
    }

    public void addMaxMode(String max_mode) {

        this.seed += "&max_mode=" + max_mode;
        
    }

    public void addTargetMode(String target_mode) {

        this.seed += "&target_mode=" + target_mode;
        
    }

    /** POPULARITY * Range: 0-100 */

    public void addMinPopularity(String min_popularity) {

        this.seed += "&min_popularity=" + min_popularity;

    }

    public void addMaxPopularity(String max_popularity) {

        this.seed += "&max_popularity=" + max_popularity;
        
    }

    public void addTargetPopularity(String target_popularity) {

        this.seed += "&target_popularity=" + target_popularity;
        
    }

    /** SPEECHINESS * Range: 0-1 */

    public void addMinSpeechiness(String min_speechiness) {

        this.seed += "&min_speechiness=" + min_speechiness;

    }

    public void addMaxSpeechiness(String max_speechiness) {

        this.seed += "&max_speechiness=" + max_speechiness;
        
    }

    public void addTargetSpeechiness(String target_speechiness) {

        this.seed += "&target_speechiness=" + target_speechiness;
        
    }

    /** TEMPO */

    public void addMinTempo(String min_tempo) {

        this.seed += "&min_tempo=" + min_tempo;

    }

    public void addMaxTempo(String max_tempo) {

        this.seed += "&max_tempo=" + max_tempo;
        
    }

    public void addTargetTempo(String target_tempo) {

        this.seed += "&target_tempo=" + target_tempo;
        
    }

    /** VALENCE * Range: 0-1 */

    public void addMinValence(String min_valence) {

        this.seed += "&min_valence=" + min_valence;

    }

    public void addMaxValence(String max_valence) {

        this.seed += "&max_valence=" + max_valence;
        
    }

    public void addTargetValence(String target_valence) {

        this.seed += "&target_valence=" + target_valence;
        
    }

    
}
