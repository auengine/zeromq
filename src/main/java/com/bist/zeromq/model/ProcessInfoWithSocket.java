package com.bist.zeromq.model;

import com.bist.zeromq.model.internal.IInternalInfo;
import com.bist.zeromq.model.internal.ProcessInfo;
import lombok.Data;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProcessInfoWithSocket implements IInternalInfo
{
    private ProcessInfo processInfo;
    private Map<String, ZMQ.Socket> socketMap= new HashMap<>();


    public ProcessInfoWithSocket(ProcessInfo processInfo,ZMQ.Socket in, ZMQ.Socket out)
    {
       this.processInfo = processInfo;
       //this.socketMap.put(processInfo.getIn(),in);
      //this.socketMap.put(processInfo.getOut(),out);
    }

}
