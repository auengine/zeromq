package com.bist.zeromq.model.internal;

import lombok.Data;



@Data
public class PeerInfo implements IInternalInfo
{
   private String name;
   private String ip;
   private int port;

    public PeerInfo(final String name, final String ip, final int port)
    {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }


    public String printStr(){
        return name + "---> " +ip + ":" + port;

    }


}
