package com.bist.zeromq.service;

import com.bist.zeromq.config.MessageSize;
import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.model.transfer.Query;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QueryService
{

    private static final Map<QueryType, Map<MessageSize, Query>> queryMap = new HashMap<>();



    public static Query createQuery(QueryType queryType, MessageSize messageSize) throws IOException
    {
        Query query = new Query(queryType, messageSize);
        return query;
    }
}
