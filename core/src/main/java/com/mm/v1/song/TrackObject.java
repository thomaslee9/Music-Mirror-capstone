package com.mm.v1.song;

import java.util.ArrayList;
import java.util.List;

import com.mm.v1.album.AlbumObject;
import com.mm.v1.artist.ArtistObject;
import com.mm.v1.meta.ExternalIDs;
import com.mm.v1.meta.ExternalURLs;
import com.mm.v1.meta.LinkedFrom;
import com.mm.v1.meta.Restrictions;

public class TrackObject {

    private AlbumObject album;
    private List<ArtistObject> artists;
    private List<String> available_markets;
    private int disc_number;
    private int duration_ms;
    private boolean explicit;
    private ExternalIDs external_ids;
    private ExternalURLs external_urls;
    private String href;
    private String id;
    private boolean is_playable;
    private LinkedFrom linked_from;
    private Restrictions restrictions;
    private String name;
    private int popularity;
    private String preview_url;
    private int track_number;
    private String type;
    private String uri;
    private boolean is_local;

    public String getName() {
        return this.name;
    }

    public AlbumObject getAlbum() {
        return this.album;
    }

    public List<ArtistObject> getArtists()  {
        return this.artists;
    }

    public String getArtistString() {

        List<String> artist_names = new ArrayList<String>();

        for (ArtistObject artist : this.getArtists()) {
            artist_names.add(artist.getName());
        }
        String artist_string = "";
        for (String curr_artist : artist_names) {
            artist_string += curr_artist + " ";
        }

        return artist_string;

    }


    public String getArtistStringComma() {

        List<String> artist_names = new ArrayList<String>();

        for (ArtistObject artist : this.getArtists()) {
            artist_names.add(artist.getName());
        }
        String artist_string = "";
        for (String curr_artist : artist_names) {
            artist_string += curr_artist + ", ";
        }

        if (artist_string.length() > 2) {
            artist_string = artist_string.substring(0, artist_string.length() - 2);
        }

        return artist_string;

    }


    // naively get the first genre of the first artist
    public String[] getGenres()    {

        ArtistObject artist = this.getArtists().get(0);
        List<String> genres = artist.getGenres();

        String[] array;
        if (genres != null) { array = genres.toArray(new String[0]); }
        else { array = new String[0]; }

        return array;

    }

    public String getFirstArtistId()  {

        ArtistObject first_artist = this.artists.get(0);
        return first_artist.getId();

    }

    public String getId()   {
        return this.id;
    }

    public int getDuration()    {
        return this.duration_ms;
    }
    
}
