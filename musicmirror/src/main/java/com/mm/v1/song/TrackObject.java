package com.mm.v1.song;

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

    public String getId()   {
        return this.id;
    }
    
}
