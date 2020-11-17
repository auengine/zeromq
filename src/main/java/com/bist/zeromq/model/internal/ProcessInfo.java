package com.bist.zeromq.model.internal;

import com.bist.zeromq.config.AppType;
import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.config.TrtType;
import lombok.Data;

import java.util.List;

@Data
public class ProcessInfo  implements IInternalInfo
{
    private String name;
    private AppType type;
    private String ipcPath;
    private List<QueryType> queryTypes;
    private List<TrtType> trtTypes;


    public ProcessInfo(final String name,AppType type, final String ipcPath, List<QueryType> types,
        List<TrtType> trtTypes)
    {
        this.name = name;
        this.ipcPath = ipcPath;
        this.type = type;
        this.queryTypes = types;
        this.trtTypes = trtTypes;

    }

    public String printStr(){
        return type + "---> " +ipcPath + "--->" + name;
    }

}
