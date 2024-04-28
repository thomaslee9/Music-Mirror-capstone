package com.mm.v1;

import java.util.ArrayList;
import java.util.List;

import com.mm.v1.artist.ArtistObject;
import com.mm.v1.requests.AddToPlaybackRequest;
import com.mm.v1.requests.SearchRequest;
import com.mm.v1.requests.StartPlaybackRequest;
import com.mm.v1.responses.SearchResponse;
import com.mm.v1.semanticmatch.SemanticMatch;
import com.mm.v1.song.TrackObject;
import com.mm.v1.song.Tracks;

public class SpotifyPlaybackController {

    private static Double search_threshold = 0.85;
    private static String device_id = "83e2221a51a366dbca4e16114644ef9a6ad165e9";
    private String access_token;
    private boolean first_play;

    public SpotifyPlaybackController(String access_token)   {
        this.access_token = access_token;
        this.first_play = true;
    }

    public String queueSong(String song_name, String artist_name) {

        System.out.println("### Searching to Queue Song - Song Name: " + song_name + " - Artist Name: " + artist_name + "###");
        
        TrackObject found_track;
        // make the first search attempt for the song
        found_track = this.searchForTrack(this.access_token, song_name, artist_name, 1);
        
        // if the result is still null, we didn't find, so try again
        if (found_track == null)    {
            System.out.println("*** Searching Again ***");
            found_track = this.searchForTrack(this.access_token, song_name, artist_name, 2);
        }

        // at this point if we are still null then we failed
        if (found_track == null)    { return ""; }

        System.out.println("### Found Song ###");
        // now we can get the song_id from the song and queue it
        String song_id = found_track.getId();

        System.out.println("### Adding to Queue ###");

        AddToPlaybackRequest p = new AddToPlaybackRequest();
        p.addToQueue(access_token, song_id, device_id);

        System.out.println("### Starting Playback ###");

        StartPlaybackRequest playback = new StartPlaybackRequest();
        if (this.first_play) {
            playback.skipToNext(access_token);
        }
        playback.startPlayback(access_token);
        
        this.first_play = false;

        return song_id;

    }

    public boolean queueSong(String song_id)    {

        System.out.println("### Adding to Queue ###");

        AddToPlaybackRequest p = new AddToPlaybackRequest();
        p.addToQueue(access_token, song_id, device_id);

        System.out.println("### Starting Playback ###");

        StartPlaybackRequest playback = new StartPlaybackRequest();
        if (this.first_play)    {
            playback.skipToNext(access_token);
        }
        playback.startPlayback(access_token);

        this.first_play = false;

        return true;

    }

    public TrackObject getSong(String song_name, String artist_name)    {
        
        System.out.println("### Searching to Get Song - Song Name: " + song_name + " - Artist Name: " + artist_name + "###");
        
        TrackObject found_track;
        // make the first search attempt for the song
        found_track = this.searchForTrack(this.access_token, song_name, artist_name, 1);
        
        // if the result is still null, we didn't find, so try again
        if (found_track == null)    {
            System.out.println("*** Searching Again ***");
            found_track = this.searchForTrack(this.access_token, song_name, artist_name, 2);
        }

        // at this point if we are still null then we failed
        if (found_track == null)    { return null; }

        System.out.println("### Found Song ###");
        return found_track;

    }

    private TrackObject searchForTrack2(String access_token, String song_name, String artist_name, int encoding_scheme)    {

        SearchResponse search_response = new SearchRequest().searchForTrack(access_token, song_name, artist_name, 1);

        Tracks tracks = search_response.getTracks();
        TrackObject result = null;

        for (TrackObject t : tracks.getTrackItems())    {

            String curr_track_name = t.getName();
            String curr_artist_string = t.getArtistString();

            System.out.println("Search Result - Song Name: " + curr_track_name + " ### Artist Name: " + curr_artist_string);

            if (this.is_semantic_match(song_name, artist_name, curr_track_name, curr_artist_string))   {
                System.out.println("** Found Exact Match **");
                result = t;
                break; 
            }
        }
        return result;

    }

    private TrackObject searchForTrack(String access_token, String song_name, String artist_name, int encoding_scheme)    {

        SearchResponse search_response = new SearchRequest().searchForTrack(access_token, song_name, artist_name, 1);

        Tracks tracks = search_response.getTracks();
        TrackObject result = null;

        double max_semantic_similarity = 0;

        for (TrackObject t : tracks.getTrackItems())    {

            String curr_track_name = t.getName();
            String curr_artist_string = t.getArtistString();

            System.out.println("Search Result - Song Name: " + curr_track_name + " ### Artist Name: " + curr_artist_string);

            double semantic_similarity = this.get_semantic_match(song_name, artist_name, curr_track_name, curr_artist_string);
            
            // if we have found the new highest semantic match then use this as the result
            if (semantic_similarity > max_semantic_similarity)  {
                System.out.println("** New highest match: similarity = " + semantic_similarity);
                result = t;
                max_semantic_similarity = semantic_similarity;
            }

        }

        // now check that the max similarity (associated w the result) meets minimum
        if (max_semantic_similarity >= search_threshold)    { return result; }
        else    { return null; }

    }

    private boolean is_semantic_match(String song_name, String artist_name, String curr_name, String curr_artist) {

        String input = song_name + " " + artist_name;
        String output = curr_name + " " + curr_artist;

        Double similarity = SemanticMatch.computeCosineSimilarity(input, output);

        System.out.println("Similarity: " + similarity);

        if (similarity >= search_threshold) { return true; }
        else    { return false; }

    }

    private double get_semantic_match(String song_name, String artist_name, String curr_name, String curr_artist) {

        String input = song_name + " " + artist_name;
        String output = curr_name + " " + curr_artist;

        Double similarity = SemanticMatch.computeCosineSimilarity(input, output);

        System.out.println("Similarity: " + similarity);

        return similarity;

    }
    
}
