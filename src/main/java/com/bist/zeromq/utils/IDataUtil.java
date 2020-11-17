package com.bist.zeromq.utils;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.model.transfer.IMessage;
import com.bist.zeromq.model.internal.IInternalInfo;

import java.io.IOException;
import java.io.Serializable;

@Deprecated
public class IDataUtil
{

    public static String createCommand(IInternalInfo data,CommandCode commandCode) throws IOException
    {
        Command command = new Command(commandCode, data);
        String base64 = EncodingUtils.encode(command);
        return base64;
    }



    public static IInternalInfo decodeData(String data) throws IOException
    {
       // Serializable o = EncodingUtils.decode(data);
        return null; //(IInternalInfo)o;
    }

    public static boolean isCommand(Object o)
    {
        return o != null && o instanceof IMessage;
    }

    public static IMessage getCommand(Object o)
    {
        if (o != null && o instanceof IMessage)
        {
            return (IMessage)o;
        }
        throw new IllegalArgumentException("Not command.");
    }
}