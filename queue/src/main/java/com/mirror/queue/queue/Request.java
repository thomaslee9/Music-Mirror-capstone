package com.mirror.queue.queue;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    // String Name User input
    private String songName;
    // String Artist User input
    private String songArtist;
    // Requesting User
    private String username;
    // Communication Type
    private MessageType type;

    public String getUser() {
        return username;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }
}
