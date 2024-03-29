package com.mm.v3;

import java.io.Serializable;    

public class MessageResponse implements Serializable {

    private String song_id;
    private String song_name;
    private String artist_name;

    public MessageResponse(String song_id, String song_name, String artist_name)  {

        this.song_id = song_id;
        this.song_name = song_name;
        this.artist_name = artist_name;

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
    
}
