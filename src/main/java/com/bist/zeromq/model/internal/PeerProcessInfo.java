package com.bist.zeromq.model.internal;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PeerProcessInfo implements IInternalInfo
{
    private PeerInfo peerInfo;
    private ProcessInfo processInfo;

    public PeerProcessInfo(final PeerInfo peerInfo, final ProcessInfo processInfo)
    {
        this.peerInfo = peerInfo;
        this.processInfo = processInfo;
    }
}
