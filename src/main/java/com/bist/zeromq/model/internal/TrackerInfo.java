package com.bist.zeromq.model.internal;

import lombok.Data;


@Data
public class TrackerInfo implements IInternalInfo
{
   private String name;
   private String ip;
   private int commandPort;
   private int publisherPort;

    public TrackerInfo(final String name, final String ip, final int commandPort,final int publisherPort)
    {
        this.name = name;
        this.ip = ip;
        this.commandPort = commandPort;
        this.publisherPort = publisherPort;
    }



}
