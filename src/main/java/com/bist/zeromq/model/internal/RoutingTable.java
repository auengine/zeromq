package com.bist.zeromq.model.internal;

import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.config.TrtType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RoutingTable implements IInternalInfo
{

    Map<QueryType, PeerProcessInfo> queryTable = new HashMap<>();
    Map<TrtType, PeerProcessInfo> trtTable = new HashMap<>();

    public void addRoute(PeerProcessInfo p)
    {
        ProcessInfo processInfo= p.getProcessInfo();
        if(processInfo!=null){
            if(processInfo.getQueryTypes()!=null){
                for (QueryType queryType: processInfo.getQueryTypes())
                {
                    queryTable.put(queryType,p);
                }
            }
            if(processInfo.getTrtTypes()!=null){
                for (TrtType trtType: processInfo.getTrtTypes())
                {
                    trtTable.put(trtType,p);
                }
            }
        }
    }

    public String buildTableStr(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n****Table Content****\n");

        for (Map.Entry<QueryType,PeerProcessInfo> q:queryTable.entrySet()
             )
        {
            builder.append("Query Type:" +q.getKey() +
                    " Process: " + q.getValue().getProcessInfo().printStr() +
                    " Peer: " + q.getValue().getPeerInfo().printStr() +"\n" );
        }

        return  builder.toString();
    }



}
