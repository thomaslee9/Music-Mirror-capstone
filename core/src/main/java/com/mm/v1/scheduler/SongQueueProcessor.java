package com.mm.v1.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mm.v1.SpotifyPlaybackController;
import com.mm.v1.queue.Song;
import com.mm.v1.queue.SongQueue;

public class SongQueueProcessor {

    private SongQueue sq;
    private ScheduledExecutorService executor;
    private String access_token;
    private boolean first_song;
    
    private static int buffer = 5000;

    public SongQueueProcessor(SongQueue sq) {
        this.sq = sq;
        // can be single threaded to start
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.access_token = "";
        this.first_song = true;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public void startProcessing()   {
        this.processNextSong();
    }

    public void processNextSong()   {

        System.out.println("__SCHEDULER__: processing next song");
        // get the duration of the currently playing song
        long duration;
        if (this.first_song)    {
            System.out.println("__SCHEDULER__: first song, adding some buffer time");
            duration = getCurrentSongDuration() - buffer;
        }
        else    {
            duration = getCurrentSongDuration();
        }
        this.first_song = false;

        executor.schedule( () -> {

            System.out.println("__SCHEDULER__: timer reached, queueing song now");
            // get the next song in the queue (the one we should queue)
            String song_id = getNextSong();
            // then actually queue this song 
            SpotifyPlaybackController P = new SpotifyPlaybackController(this.access_token);
            boolean result = P.queueSong(song_id);

            sq.pop();

            // process the next song recursively
            processNextSong();

        }, duration, TimeUnit.MILLISECONDS);

    }

    public String getNextSong() {

        // peek at the next song in the queue
        Song next_song = this.sq.peekSecondElement();
        if (next_song == null)  {
            System.out.println("__SCHEDULER__: failed to get next song from queue");
        }
        return next_song.getSongId();

    } 

    public long getCurrentSongDuration()    {

        Song curr_song = this.sq.peek();
        if (curr_song == null)  {
            System.out.println("__SCHEDULER__: failed to get curr song duration from queue");
        }
        return curr_song.getDuration();

    }
    
}
