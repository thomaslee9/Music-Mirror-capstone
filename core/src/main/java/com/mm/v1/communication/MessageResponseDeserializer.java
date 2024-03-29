package com.mm.v1.communication;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

import com.mm.v3.MessageResponse;

public class MessageResponseDeserializer {

    public static MessageResponse deserialize(String serializedMessage) throws Exception {

        byte[] bytes = Base64.getDecoder().decode(serializedMessage);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);

        return (MessageResponse) ois.readObject();

    }
    
}
