package com.bist.zeromq.config;

import java.util.ArrayList;
import java.util.List;

public enum MessageType
{
    //QUERY_TYPE0(MessageKind.QUERY, 0),
    QUERY_TYPE1(MessageKind.QUERY, 1),
    QUERY_TYPE2(MessageKind.QUERY, 2),
   // TRT_TYPE0(MessageKind.TRANSACTION, 3),
    TRT_TYPE1(MessageKind.TRANSACTION, 4),
    TRT_TYPE2(MessageKind.TRANSACTION, 5),
    TYPE_UNKNOWN(MessageKind.TYPE_UNKNOWN,-1);

    private final int code;
    private MessageKind kind;

    MessageType(MessageKind kind,int code)

    {
        this.kind=kind;
        this.code = code;
    }

    public static List<MessageType> asEnum(List<Integer> list)
    {
        List<MessageType> result = new ArrayList<>();
        for (Integer i : list)
        {
            MessageType q = getByCode(i);
            if (q != TYPE_UNKNOWN)
            {
                result.add(q);
            }
        }

        return result;
    }

    public static MessageType getByCode(int code)
    {
        for (MessageType type : values())
        {
            if (type.getCode() == code)
            {
                return type;
            }
        }
        return TYPE_UNKNOWN;
    }

    public int getCode()
    {
        return code;
    }

    public MessageKind getKind()
    {
        return kind;
    }

    public boolean isQuery()
    {

        return this.getKind()==MessageKind.QUERY;
    }

    public boolean isTrt()
    {

        return this.getKind()==MessageKind.TRANSACTION;
    }



}
