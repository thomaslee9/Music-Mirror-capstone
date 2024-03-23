package com.mm.v1.queue;

public class Song {
    private String songName;
    private String songArtist;
    private String id;
    private String username;
    private int likeScore;

    public Song(String songName, String songArtist, String id, String username) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.id = id;
        this.username = username;
        this.likeScore = 1;
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

    public void like() {
        likeScore += 1;
    }

    public void dislike() {
        likeScore -= 1;
    }

    public int getLikes() {
        return likeScore;
    }
}
