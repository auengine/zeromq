package com.bist.zeromq.model.internal;

import com.bist.zeromq.config.AppType;
import com.bist.zeromq.config.QueryType;
import com.bist.zeromq.config.TrtType;
import lombok.Data;

import java.util.List;
import java.util.Objects;

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
        return "PType: " + type + " ipc: " +ipcPath + " name: " + name;
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
        final ProcessInfo that = (ProcessInfo)o;
        return Objects.equals(name, that.name) &&
            type == that.type &&
            Objects.equals(ipcPath, that.ipcPath) &&
            Objects.equals(queryTypes, that.queryTypes) &&
            Objects.equals(trtTypes, that.trtTypes);
    }

    public int hashCode()
    {
        return Objects.hash(name, type, ipcPath, queryTypes, trtTypes);
    }

}
