package com.bist.zeromq.model;

import com.bist.zeromq.model.internal.*;
import lombok.Getter;

@Getter
public class ZeroTrackerRoutingInfo
{
    private TrackerInfo currentTrackerInfo;
    private RoutingTable routingTable = new RoutingTable();

    public ZeroTrackerRoutingInfo(final String name, final String ip, final int port,final int pubPort)
    {
        this.currentTrackerInfo= new TrackerInfo(name,ip,port,pubPort);
    }

    public void  registerNewProcess(PeerProcessInfo peerProcessInfo){
        this.routingTable.addRoute(peerProcessInfo);
    }



}
