package com.bist.zeromq.model;

import com.bist.zeromq.model.internal.PeerInfo;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.internal.RoutingTable;
import lombok.Getter;

@Getter
public class ZeroPeerRoutingInfo
{
    private PeerInfo currentPeerInfo;
    private RoutingTable routingTable;

    public ZeroPeerRoutingInfo(final String name, final String ip, final int port)
    {
        this.currentPeerInfo= new PeerInfo(name,ip,port);
    }

    public void  update(RoutingTable routingTable){
         this.routingTable=routingTable;
    }

    public void getStream(){

    }



}
