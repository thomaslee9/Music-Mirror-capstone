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

    public void updateSongId(String queue_id, String song_id)  {
        Song song = map.get(queue_id);
        song.updateSongId(song_id);
    }

    public void removeById(String songId) {
        map.remove(songId);
    }

    public Song getSongById(String songId) {
        return map.get(songId);
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