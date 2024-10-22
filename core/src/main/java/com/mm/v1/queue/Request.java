package com.mm.v1.queue;

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
    // UserId
    private String userId;

    public String getUser() {
        return username;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public MessageType getMessageType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }


    public void setUser(String username) {
    this.username = username;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setMessageType(MessageType type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
