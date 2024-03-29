package com.mm.v2.communication;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import com.mm.v3.MessageResponse;

public class MessageResponseSerializer {

    public static String serialize(MessageResponse message) throws Exception    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(message);
        oos.flush();

        byte[] bytes = bos.toByteArray();

        return Base64.getEncoder().encodeToString(bytes);

    }
    
}
