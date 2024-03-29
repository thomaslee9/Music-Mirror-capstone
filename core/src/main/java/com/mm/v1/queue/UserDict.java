package com.mm.v1.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDict {
    ConcurrentHashMap<String, ConcurrentLinkedDeque<Song>> map;

    public UserDict() {
        this.map = new ConcurrentHashMap<>();
    }

    public void addUser(String userId) {
        map.put(userId, new ConcurrentLinkedDeque<Song>());
    }

    public void addSong(String userId, Song song) {
        map.get(userId).addLast(song);
    }


    public void removeSong(String userId, Song song) {
        map.get(userId).remove(song);
    }

    public ConcurrentLinkedDeque<Song> getUserSongs(String userId) {
        return map.get(userId);
    }
}