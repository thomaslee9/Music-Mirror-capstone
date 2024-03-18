package com.mm.v2;

import java.util.HashMap;
import java.util.Map;

import com.mm.v2.song.SongAudioFeatures;

public class SongAttributeDatabase {
    
    private Map<String, SongAudioFeatures> data;

    public SongAttributeDatabase()  {
        this.data = new HashMap<String, SongAudioFeatures>();
    }

    public void AddSong(String song_id, SongAudioFeatures features) {
        this.data.put(song_id, features);
    }

    public void RemoveSong(String song_id)  {
        this.data.remove(song_id);
    }

    public SongAudioFeatures GetAudioFeatures(String song_id)   {
        return this.data.get(song_id);
    }

}
