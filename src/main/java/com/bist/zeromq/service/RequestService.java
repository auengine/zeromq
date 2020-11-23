package com.bist.zeromq.service;

import com.bist.zeromq.config.MessageSize;
import com.bist.zeromq.config.MessageType;
import com.bist.zeromq.model.transfer.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestService
{

    private static final Map<MessageType, Map<MessageSize, Request>> requestMap = new HashMap<>(2);

   public static Request createOrGet(MessageType messageType, MessageSize messageSize)
    {
        if(requestMap.containsKey(messageType)){
            if(requestMap.get(messageType).containsKey(messageSize)){
                return requestMap.get(messageType).get(messageSize);
            }else{
                Request request = new Request(messageType, messageSize);
                requestMap.get(messageType).put(messageSize,request);
                return request;
            }
        }

        Request request = new Request(messageType, messageSize);
        Map<MessageSize, Request> map= new HashMap<>(2);
        map.put(messageSize,request);
        requestMap.put(messageType,map);

        return request;

    }
}
