package com.mm.v3;

import java.io.Serializable;    

public class MessageResponse implements Serializable {

    private String song_id;
    private String song_name;
    private String artist_name;
    private int duration_ms;

    public MessageResponse(String song_id, String song_name, String artist_name, int duration_ms)  {

        this.song_id = song_id;
        this.song_name = song_name;
        this.artist_name = artist_name;
        this.duration_ms = duration_ms;

    }

    public String getSongId()   {
        return this.song_id;
    }

    public String getSongName() {
        return this.song_name;
    }

    public String getArtistName()   {
        return this.artist_name;
    }

    public int getDuration()    {
        return this.duration_ms;
    }
    
}
