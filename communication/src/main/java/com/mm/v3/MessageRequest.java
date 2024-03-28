package com.mm.v3;

import java.io.Serializable;
import java.util.List;

import org.javatuples.Pair;

public class MessageRequest implements Serializable {

    private int message_id;
    private String song_id;
    private String artist_id;
    private List<Pair<String, Integer>> session;

    public MessageRequest(int message_id, String song_id, String artist_id, List<Pair<String, Integer>> session)   {

        this.message_id = message_id;
        this.song_id = song_id;
        this.artist_id = artist_id;
        this.session = session;

    }

    public int getMessageId()   {
        return this.message_id;
    }

    public String getSongId()   {
        return this.song_id;
    }

    public String getArtistId() {
        return this.artist_id;
    }

    public List<Pair<String, Integer>> getSession() {
        return this.session;
    }
    
}
