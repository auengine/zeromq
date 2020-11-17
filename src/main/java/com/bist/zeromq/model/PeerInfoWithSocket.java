package com.bist.zeromq.model;

import com.bist.zeromq.model.internal.IInternalInfo;
import com.bist.zeromq.model.internal.PeerInfo;
import lombok.Data;
import org.zeromq.ZMQ;

@Data
public class PeerInfoWithSocket implements IInternalInfo
{
    private PeerInfo peerInfo;
    private ZMQ.Socket socket;


    public PeerInfoWithSocket(PeerInfo peerInfo, ZMQ.Socket out)
    {
        this.peerInfo = peerInfo;
        this.socket= out;
    }



}
