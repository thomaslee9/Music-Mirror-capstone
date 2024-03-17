package com.mm.v1.queue;

public class Song {
    private String songName;
    private String songArtist;
    private String id;
    private String username;

    public Song(String songName, String songArtist, String id, String username) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.id = id;
        this.username = username;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
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
