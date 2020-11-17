package com.bist.zeromq.config;

import java.util.ArrayList;
import java.util.List;

public enum TrtType
{
    TYPE0(0),
    TYPE1(1),
    TYPE2(2),
    TYPE_UNKNOWN(-1);

    private final int code;

    TrtType(int code)
    {
        this.code = code;
    }


    public static List<TrtType> asEnum(java.util.List<Integer> list)
    {
        List<TrtType> result = new ArrayList<>();
        for (Integer i : list)
        {
            com.bist.zeromq.config.TrtType q = getByCode(i);
            if (q != TYPE_UNKNOWN)
            {
                result.add(q);
            }
        }

        return result;
    }

    public static TrtType getByCode(int code)
    {
        for (TrtType type : values())
        {
            if (type.code == code)
            {
                return type;
            }
        }
        return TYPE_UNKNOWN;
    }
}
