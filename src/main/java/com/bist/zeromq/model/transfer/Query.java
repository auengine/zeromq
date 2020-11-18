package com.bist.zeromq.model.transfer;

import com.bist.zeromq.config.MessageSize;
import com.bist.zeromq.config.QueryType;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public class Query implements IMessage
{
    private final QueryType queryType;
    private final MessageSize requestedAnswerSize;
    private byte[] serialized;

    public Query(final QueryType queryType, final MessageSize requestedAnswerSize)
    {
        this.queryType = queryType;
        this.requestedAnswerSize = requestedAnswerSize;
    }

    public static Query  decodedForm(byte[] q)
    {
       ByteBuffer buffer = ByteBuffer.wrap(q);
       int index = 0;
       QueryType type =QueryType.getByCode(buffer.getInt(index));
       index+=Integer.BYTES;
       MessageSize size = MessageSize.values()[buffer.getInt(index)] ;

       return new Query(type,size);
    }

    public byte[] encodedForm()
    {
        if (serialized == null)
        {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            int index = 0;
            buffer.putInt(index, queryType.getCode());
            index += Integer.BYTES;
            buffer.putInt(index, requestedAnswerSize.ordinal());
            serialized = buffer.array();
        }
        return serialized;

    }
}
