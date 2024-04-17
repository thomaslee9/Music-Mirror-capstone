package com.mm.v1.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.javatuples.Pair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SongQueue {
    private ConcurrentLinkedQueue<Song> queue;

    public SongQueue() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void push(Song song) {
        queue.add(song);
    }

    public Song pop() {
        return queue.poll();
    }

    public boolean remove(Song song) {
        return queue.remove(song);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Song peek() {
        return queue.peek();
    }

    public Song peekSecondElement() {

        if (queue.size() < 2) {
            return null;
        }

        Song firstSong = null;
        Song secondSong = null;

        // Iterate over the elements of the queue
        for (Song song : this.queue) {
            if (firstSong == null) {
                firstSong = song;
            } else {
                secondSong = song;
                break; // Found the second element, exit the loop
            }
        }

        return secondSong;
    }

    public void printQueue() {
        System.out.println("Song Queue: \n");

        for (Song song : queue) {
            System.out.println(song + "\n");
        }
    }

    public List<Pair<String, Integer>> getSession() {

        List<Pair<String, Integer>> session = new ArrayList<Pair<String, Integer>>();

        for (Song song : queue) {

            // create a pair of the song's id and likes
            String song_id = song.getSongId();
            int likes = song.getLikes();

            Pair<String, Integer> pair = new Pair<String, Integer>(song_id, likes);
            session.add(pair);
        }
        return session;

    }

    // Main Method for continually accepting song messages
    public static void main(String[] args) {
        // SongQueue testQueue = new SongQueue();

        // int i = 0;

        // // Continuous Request Acceptor loop
        // while (true) {

        // Song newSong = new Song("test", "testID_" + i, "testUser");
        // i++;

        // // Add New Song to Queue
        // testQueue.push(newSong);
        // testQueue.printQueue();

        // if (i >= 10) {
        // break;
        // }
        // }
    }

}