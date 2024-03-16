package com.mirror.queue.queue;

public class Song {
    private String name;
    private String id;

    public Song(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Song { name: '" + name + "' id: '" + id + "' }";
    }
}
