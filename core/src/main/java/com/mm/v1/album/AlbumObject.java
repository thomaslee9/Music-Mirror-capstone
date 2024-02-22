package com.mm.v1.album;

import java.util.List;

import com.mm.v1.artist.SimplifiedArtistObject;
import com.mm.v1.media.ImageObject;
import com.mm.v1.meta.ExternalURLs;
import com.mm.v1.meta.Restrictions;

public class AlbumObject {

    private String album_type;
    private int total_tracks;
    private List<String> available_markets;
    private ExternalURLs external_urls;
    private String href;
    private String id;
    private List<ImageObject> images;
    private String name;
    private String release_date;
    private String release_date_precision;
    private Restrictions restrictions;
    private String type;
    private String uri;
    private List<SimplifiedArtistObject> artists;

    public String getName() {
        return this.name;
    }

}
