package com.mm.v1.queue;

public class Song {
    private String songName;
    private String songArtist;
    private String queue_id;
    private String song_id;
    private String username;
    private int likeScore;

    public Song(String songName, String songArtist, String queue_id, String song_id, String username) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.queue_id = queue_id;
        this.song_id = song_id;
        this.username = username;
        this.likeScore = 1;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getQueueId() {
        return queue_id;
    }

    public String getSongId() {
        return song_id;
    }

    public String getUser() {
        return username;
    }

    @Override
    public String toString() {
        return "Song { Title: '" + songName + "' queue_id: '" + queue_id + "' song_id: '" + song_id + "' User: '" + username + "' }";
    }

    public void updateSongId(String song_id)  {
        this.song_id = song_id;
    }

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
