package com.mirror.queue.queue;

public class Song {
    private String songName;
    private String id;
    private String username;

    public Song(String songName, String id, String username) {
        this.songName = songName;
        this.id = id;
        this.username = username;
    }

    public String getSongName() {
        return songName;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return username;
    }

    @Override
    public String toString() {
        return "Song { Title: '" + songName + "' id: '" + id + "' User: '" + username + "' }";
    }
}
