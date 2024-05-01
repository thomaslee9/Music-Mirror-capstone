package com.mm.v1.queue;

import java.util.concurrent.ConcurrentHashMap;

public class Song {
    private String songName;
    private String songArtist;
    //private String commaSeparatedArtists;
    private String queueId;
    private String songId;
    private String username;
    private String userId;
    private boolean isRec;
    private boolean recComplete;
    private boolean isPlaying;
    private ConcurrentHashMap<String, String> colorMap;
    private int likeScore;
    private int duration_ms;

    public Song(String songName, String songArtist, String queueId, String songId, String username, String userId,
            boolean isRec) {
        this.songName = songName;
        this.songArtist = songArtist;
        //this.commaSeparatedArtists = songArtist;
        this.queueId = queueId;
        this.songId = songId;
        this.username = username;
        this.userId = userId;
        this.colorMap = new ConcurrentHashMap<String, String>();
        this.colorMap.put(userId, "green");
        this.likeScore = 1;
        this.isRec = isRec;
        this.recComplete = false;
        this.isPlaying = false;
    }

    public void setRecComplete() {
        this.recComplete = true;
    }

    public void setPlaying() {
        this.isPlaying = true;
    }

    public void setNotPlaying() {
        this.isPlaying = false;
    }

    public void setDuration(int duration_ms)    {
        this.duration_ms = duration_ms;
    }


    public void setColor(String color, String userId) {

        colorMap.put(userId, color);
        System.out.println("Color Map: " + colorMap);
    }

    public String getColor(String userId) {
        return colorMap.get(userId);
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    // public String getCommaSeparatedArtists() {
    //     return commaSeparatedArtists;
    // }

    public String getQueueId() {
        return queueId;
    }

    public String getSongId() {
        return songId;
    }

    public String getUser() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public int getDuration()    {
        return duration_ms;
    }

    @Override
    public String toString() {
        return "Song { Title: '" + songName + "' queueId: '" + queueId + "' songId: '" + songId + "' username: '"
                + username + "' colorMap: '" + colorMap + "' isRec: '" + isRec + "' recComplete: '" + recComplete + "' isPlaying: '" + isPlaying + "'}";
    }

    public void updateSongId(String songId) {
        this.songId = songId;
    }

    public void updateSongName(String song_name) {
        this.songName = song_name;
    }

    public void updateArtistName(String artist_name) {
        this.songArtist = artist_name;
    }

    // public void updateCommaSeparatedArtists(String artist_name) {
    //     this.commaSeparatedArtists = artist_name;
    // }

    public void like(int count) {
        likeScore += count;
    }

    public void dislike(int count) {
        likeScore -= count;
    }

    public int getLikes() {
        return likeScore;
    }
}
