package com.bist.zeromq.config;

import java.util.ArrayList;
import java.util.List;

public enum MessageKind
{
    QUERY(0),
    TRANSACTION(1),
    TYPE_UNKNOWN(-1);

    private final int code;

    MessageKind(int code)
    {
        this.code = code;
    }

    public static List<MessageKind> asEnum(List<Integer> list)
    {
        List<MessageKind> result = new ArrayList<>();
        for (Integer i : list)
        {
            MessageKind q = getByCode(i);
            if (q != TYPE_UNKNOWN)
            {
                result.add(q);
            }
        }

        return result;
    }

    public static MessageKind getByCode(int code)
    {
        for (MessageKind type : values())
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


}
