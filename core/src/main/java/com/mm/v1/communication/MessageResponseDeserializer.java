package com.mm.v1.communication;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

public class MessageResponseDeserializer {

    public static MessageRequest deserialize(String serializedMessage) throws Exception {

        byte[] bytes = Base64.getDecoder().decode(serializedMessage);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return (MessageRequest) ois.readObject();

    }
    
}
