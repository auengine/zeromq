package com.bist.zeromq.model.transfer;

import com.bist.zeromq.config.MessageSize;
import com.bist.zeromq.config.MessageType;
import com.bist.zeromq.service.RequestService;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public class Request implements IMessage
{
    private final MessageType messageType;
    private final MessageSize requestedAnswerSize;
    private byte[] serialized;
    private static int byteSize=8;

    public static int getByteSize(){
        return byteSize;
    }

    public Request(final MessageType messageType, final MessageSize requestedAnswerSize)
    {
        this.messageType = messageType;
        this.requestedAnswerSize = requestedAnswerSize;
    }

    public static Request decodedForm(byte[] q)
    {
        //reverse order
       ByteBuffer buffer = ByteBuffer.wrap(q);
       int index = 0;
       MessageType messageType =MessageType.getByCode(buffer.getInt(index));
       index+=Integer.BYTES;
       MessageSize size = MessageSize.getByCode(buffer.getInt(index));

       return RequestService.createOrGet(messageType,size);
    }

    public byte[] encodedForm()
    {
        if (serialized == null)
        {
            ByteBuffer buffer = ByteBuffer.allocate(byteSize);
            int index = 0;
            buffer.putInt(index, messageType.getCode());
            index += Integer.BYTES;
            buffer.putInt(index, requestedAnswerSize.getCode());
            serialized = buffer.array();
        }
        return serialized;

    }

    public String toString(){
        return messageType.name() + "-" + requestedAnswerSize.name();
    }


}
