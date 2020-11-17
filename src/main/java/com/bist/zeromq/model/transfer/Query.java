package com.bist.zeromq.model.transfer;

import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.model.internal.IInternalInfo;
import lombok.Data;

@Data
public class Query implements IMessage
{
    private QueryType queryType;
    private IInternalInfo data;
    public Query(final QueryType queryType, final IInternalInfo data)
    {
        this.queryType = queryType;
        this.data = data;
    }
}
