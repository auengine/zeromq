package com.bist.zeromq.model;

import com.bist.zeromq.model.internal.PeerInfo;
import com.bist.zeromq.model.internal.PeerProcessInfo;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.internal.RoutingTable;
import com.bist.zeromq.model.transfer.Query;
import com.bist.zeromq.utils.ConnectionUtils;
import lombok.Getter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.*;

@Getter
public class ZeroPeerRoutingInfo
{
    private final PeerInfo currentPeerInfo;
    private final Map<ProcessInfo, ZMQ.Socket> processSocketMap = new HashMap<>();
    private final Map<PeerInfo, ZMQ.Socket> peerSocketMap = new HashMap<>();
    private final Map<ProcessInfo, ZMQ.Socket> clientSocketMap = new HashMap<>();
    private RoutingTable routingTable;


    public ZeroPeerRoutingInfo(final String name, final String ip, final int port)
    {
        this.currentPeerInfo = new PeerInfo(name, ip, port);
    }

    public void update(RoutingTable routingTable)
    {
        this.routingTable = routingTable;
    }

    public Optional<ZMQ.Socket> getNewClientSocket(ZContext context)
    {
        List<PeerProcessInfo> currentList=new ArrayList<>();
        routingTable.getClientsForPeer(currentPeerInfo,currentList);

        for (PeerProcessInfo client:currentList)
        {
            ProcessInfo processInfo =client.getProcessInfo();
           if(!clientSocketMap.containsKey(processInfo)){
               ZMQ.Socket socket = create(processInfo,context);
               processSocketMap.put(processInfo, socket);
               return Optional.of(socket);
           }

        }
       return Optional.ofNullable(null);

    }

    public ZMQ.Socket getStreamSocket(Query query, ZContext context)
    {
        PeerProcessInfo peerProcessInfo = routingTable.getQueryTable().get(query.getQueryType());
        if (peerProcessInfo == null)
        {
            throw new IllegalStateException("No route found for " + query.getQueryType());
        }
        //route to process
        if (peerProcessInfo.getPeerInfo().equals(currentPeerInfo))
        {
            ProcessInfo processInfo = peerProcessInfo.getProcessInfo();
            if (!processSocketMap.containsKey(processInfo))
            {
                ZMQ.Socket socket = create(processInfo, context);
                processSocketMap.put(processInfo, socket);
            }
            return this.processSocketMap.get(processInfo);
        }
        else
        {   //route to another peer
            PeerInfo peerInfo = peerProcessInfo.getPeerInfo();
            if (!peerSocketMap.containsKey(peerInfo))
            {
                ZMQ.Socket socket = create(peerInfo, context);
                peerSocketMap.put(peerInfo, socket);
            }
            return this.peerSocketMap.get(peerInfo);
        }

    }

    private ZMQ.Socket create(ProcessInfo processInfo, ZContext context)
    {

        switch (processInfo.getType())
        {
            //reverse
            case CLIENT:
                ZMQ.Socket socket = context.createSocket(SocketType.REP);
                socket.bind(ConnectionUtils.ipc(processInfo.getIpcPath()));
                return socket;
            case SERVER:
                socket = context.createSocket(SocketType.REQ);
                socket.connect(ConnectionUtils.ipc(processInfo.getIpcPath()));

                return socket;
            default:
                break;

        }
        throw new IllegalArgumentException("No route found for " + processInfo.printStr());
    }

    private ZMQ.Socket create(PeerInfo peerInfo, ZContext context)
    {
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        socket.connect(ConnectionUtils.tcp(peerInfo.getIp(),peerInfo.getPort()));
        return socket;
    }

}
