package com.bist.zeromq.config;

import java.util.ArrayList;
import java.util.List;

public enum QueryType
{
    TYPE0(0),
    TYPE1(1),
    TYPE2(2),
    TYPE_UNKNOWN(-1);

    private final int code;

    QueryType(int code)
    {
        this.code = code;
    }

    public static List<QueryType> asEnum(List<Integer> list)
    {
        List<QueryType> result = new ArrayList<>();
        for (Integer i : list)
        {
            QueryType q = getByCode(i);
            if (q != TYPE_UNKNOWN)
            {
                result.add(q);
            }
        }

        return result;
    }

    public static QueryType getByCode(int code)
    {
        for (QueryType type : values())
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
