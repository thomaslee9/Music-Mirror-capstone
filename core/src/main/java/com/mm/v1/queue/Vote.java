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
    private String queueId;
    private String username;
    // Vote Decision
    // 0: nothing
    // 1: Like
    // 2: Dislike
    private int vote;
    // Communication Type
    private MessageType type;
    private String color;
    private String userId;

    public String getUser() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getQueueId() {
        return queueId;
    }

    public int getVote() {
        return vote;
    }

    public MessageType getMsgType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}
