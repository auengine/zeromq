package com.bist.zeromq.model.internal;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PeerProcessInfo implements IInternalInfo
{
    private PeerInfo pearInfo;
    private ProcessInfo processInfo;

    public PeerProcessInfo(final PeerInfo pearInfo, final ProcessInfo processInfo)
    {
        this.pearInfo = pearInfo;
        this.processInfo = processInfo;
    }
}
