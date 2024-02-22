package com.mm.v1.artist;

import java.util.List;

import com.mm.v1.media.ImageObject;
import com.mm.v1.meta.ExternalURLs;
import com.mm.v1.popularity.Followers;

public class ArtistObject {

    private ExternalURLs external_urls;
    private Followers followers;
    private List<String> genres;
    private String href;
    private String id;
    private List<ImageObject> images;
    private String name;
    private int popularity;
    private String type;
    private String uri;

    public String getName() {
        return this.name;
    }

}