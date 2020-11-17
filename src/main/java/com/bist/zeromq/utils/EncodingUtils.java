package com.bist.zeromq.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtils
{


    public static String encode(Serializable o) throws IOException
    {

        try (ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream())
        {
            try (ObjectOutputStream objectStream = new ObjectOutputStream(binaryOutput))
            {
                objectStream.writeObject(o);
            }
            return Base64.getEncoder().encodeToString(binaryOutput.toByteArray());
        }

    }

    public static Serializable decode(byte[] input) throws IOException
    {
        byte[] binary = Base64.getDecoder().decode(input);
        try (ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(binary)))
        {
            return (Serializable)objectStream.readObject();
        }
        catch (ClassNotFoundException e)
        {
            throw  new IOException(e);
        }

    }
}
