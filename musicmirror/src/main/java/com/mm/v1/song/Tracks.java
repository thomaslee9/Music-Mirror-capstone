package com.mm.v1.song;

import java.util.List;

public class Tracks {

    private String href;
    private int limit;
    private String next;
    private int offset;
    private String previous;
    private int total;
    private List<TrackObject> items;

    public List<TrackObject> getTrackItems()  {
        return this.items;
    }
    
}