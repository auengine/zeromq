package com.bist.zeromq.service;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.model.internal.IInternalInfo;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.model.transfer.Query;
import com.bist.zeromq.utils.EncodingUtils;


import java.io.IOException;
import java.io.Serializable;

public class CommandService
{

    public static String createCommand(IInternalInfo data,
        CommandCode commandCode) throws IOException
    {
        Command command = new Command(commandCode, data);
        String base64 = EncodingUtils.encode(command);
        return base64;
    }

    public static Command base642Command(byte[] data) throws IOException
    {
        Serializable o = EncodingUtils.decode(data);
        return (Command)o;
    }

    public static Query createQuery(QueryType queryType) throws IOException
    {
        return new Query(queryType, null);

    }

    public static String createQueryBase64(QueryType queryType) throws IOException
    {
        String base64 = EncodingUtils.encode(createQuery(queryType));
        return base64;

    }
}
