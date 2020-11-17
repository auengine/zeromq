package com.bist.zeromq.model.transfer;

import com.bist.zeromq.config.TrtType;
import com.bist.zeromq.model.internal.IInternalInfo;
import lombok.Data;

@Data
public class Transaction implements IMessage
{

   private TrtType trtType;
    private IInternalInfo data;
    public Transaction(final TrtType trtType, final IInternalInfo data)
    {
        this.trtType = trtType;
        this.data = data;
    }
}
