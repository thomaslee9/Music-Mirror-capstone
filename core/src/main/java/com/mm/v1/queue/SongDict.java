package com.mm.v1.queue;

import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SongDict {
    ConcurrentHashMap<String, Song> map;

    public SongDict() {
        this.map = new ConcurrentHashMap<>();
    }

    public void add(Song song) {
        map.put(song.getQueueId(), song);
    }

    public void updateSong(String queue_id, String song_id, String song_name, String artist_name, int duration_ms) {
        Song song = map.get(queue_id);
        song.updateSongId(song_id);
        song.updateSongName(song_name);
        song.updateArtistName(artist_name);
        //song.updateCommaSeparatedArtists(artist_name);
        song.setDuration(duration_ms);
    }

    public void updateSongId(String queue_id, String song_id, int duration_ms) {
        Song song = map.get(queue_id);
        song.updateSongId(song_id);
        song.setDuration(duration_ms);
    }
    

    public void removeById(String songId) {
        map.remove(songId);
    }

    public Song getSongByQueueId(String queueId) {
        return map.get(queueId);
    }

    public void like(String songID, int likeCount) {
        Song song = map.get(songID);
        song.like(likeCount);
    }

    public int dislike(String songID, int likeCount) {
        Song song = map.get(songID);
        song.dislike(likeCount);
        return song.getLikes();
    }

}