package com.mm.v1.communication;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class MessageRequestSerializer {

    public static String serialize(MessageRequest message) throws Exception    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(message);
        oos.flush();

        byte[] bytes = bos.toByteArray();

        return Base64.getEncoder().encodeToString(bytes);

    }
    
}
