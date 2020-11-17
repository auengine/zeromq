package com.bist.zeromq.service;

import com.bist.zeromq.config.MessageSize;

public class AnswerService
{
    private static final byte[] ok = new byte[]{ 'O', 'K' };

    public static String getJunkMessage(MessageSize messageSize)
    {

        return "Test";
    }


    public static byte[] getOKMessage()
    {
        return ok;
    }


}
