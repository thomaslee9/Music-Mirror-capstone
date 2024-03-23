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
public class Vote {

    // String Name User input
    private String id;
    private String username;
    // Vote Decision
    // 0: nothing
    // 1: Like
    // 2: Dislike
    private int vote;
    // Communication Type
    private MessageType type;

    public String getUser() {
        return username;
    }

    public String getSongId() {
        return id;
    }

    public int getVote() {
        return vote;
    }

    public MessageType getMsgType() {
        return type;
    }
}
