package com.bist.zeromq.model.transfer;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.internal.IInternalInfo;
import lombok.Getter;

@Getter
public class Command implements IMessage
{
    private CommandCode commandCode;
    private IInternalInfo data;

    public Command(final CommandCode commandCode, final IInternalInfo data)
    {
        this.commandCode = commandCode;
        this.data = data;
    }


}
