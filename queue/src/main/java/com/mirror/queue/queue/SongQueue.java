// package com.alibou.websocket.chat;

import java.util.concurrent.ConcurrentLinkedQueue;

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

    public void printQueue() {
        System.out.println("Song Queue: \n");

        for (Song song : queue) {
            System.out.println(song + "\n");
        }
    }


    // Main Method for continually accepting song messages
    public static void main(String[] args) {
        SongQueue testQueue = new SongQueue();

        int i = 0;

        // Continuous Request Acceptor loop
        while (true) {
            
            Song newSong = new Song("test", "testID_" + i);
            i++;

            // Add New Song to Queue
            testQueue.push(newSong);
            testQueue.printQueue();

            if (i >= 10) {
                break;
            }
        }
    }

}