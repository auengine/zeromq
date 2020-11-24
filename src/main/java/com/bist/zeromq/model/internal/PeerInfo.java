package com.bist.zeromq.model.internal;

import lombok.Data;

import java.util.Objects;


@Data
public class PeerInfo implements IInternalInfo
{
    private String name;
    private String ip;
    private int commandPort;
    private int streamPort;

    public PeerInfo(final String name, final String ip, final int port, final int stport)
    {
        this.name = name;
        this.ip = ip;
        this.commandPort = port;
        this.streamPort = stport;
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
        return commandPort == peerInfo.commandPort && streamPort == peerInfo.streamPort &&
        name.equals(peerInfo.name) &&
            ip.equals(peerInfo.ip);
    }

    public int hashCode()
    {
        return Objects.hash(name, ip, commandPort,streamPort);
    }

    public String printStr()
    {
        return name + "---> " + ip + ":" + commandPort + "|" + streamPort;

    }


}
