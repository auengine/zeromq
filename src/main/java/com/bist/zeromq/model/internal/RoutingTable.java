package com.bist.zeromq.model.internal;

import com.bist.zeromq.config.AppType;
import com.bist.zeromq.config.MessageType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RoutingTable implements IInternalInfo
{
    //clients
   private List<PeerProcessInfo> clientProcesses = new ArrayList<>();
   //servers data
   private Map<MessageType, PeerProcessInfo> queryTable = new HashMap<>();
   private Map<MessageType, PeerProcessInfo> trtTable = new HashMap<>();

    public void addRoute(PeerProcessInfo p)
    {
        ProcessInfo processInfo = p.getProcessInfo();
        if (processInfo != null)
        {
            if (processInfo.getType() == AppType.CLIENT)
            {
                clientProcesses.add(p);
            }
            else if (processInfo.getType() == AppType.SERVER)
            {
                if (processInfo.getQueryTypes() != null)
                {
                    for (MessageType queryType : processInfo.getQueryTypes())
                    {
                        if(queryType.isQuery()){
                            queryTable.put(queryType, p);
                        }

                    }
                }
                if (processInfo.getTrtTypes() != null)
                {
                    for (MessageType trtType : processInfo.getTrtTypes())
                    {
                        if(trtType.isTrt()){
                            trtTable.put(trtType, p);
                        }

                    }
                }
            }
        }
    }

    public void getClientsForPeer(PeerInfo currentPeer,List<PeerProcessInfo> out)
    {
        for (PeerProcessInfo p:getClientProcesses() )
        {
            if(currentPeer.equals(p.getPeerInfo())){
                out.add(p);
            }
        }
    }

    public String buildTableStr()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("\n****Table Content****\n");

        for (Map.Entry<MessageType, PeerProcessInfo> q : queryTable.entrySet()
        )
        {
            builder.append("Query Type:" + q.getKey() +
                " Process: " + q.getValue().getProcessInfo().printStr() +
                " Peer: " + q.getValue().getPeerInfo().printStr() + "\n");
        }

        return builder.toString();
    }


}
