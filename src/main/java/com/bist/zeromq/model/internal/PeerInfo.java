package com.bist.zeromq.model.internal;

import lombok.Data;

import java.util.Objects;


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

    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final PeerInfo peerInfo = (PeerInfo)o;
        return port == peerInfo.port &&
            name.equals(peerInfo.name) &&
            ip.equals(peerInfo.ip);
    }

    public int hashCode()
    {
        return Objects.hash(name, ip, port);
    }

    public String printStr()
    {
        return name + "---> " + ip + ":" + port;

    }


}
