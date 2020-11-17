package com.bist.zeromq.utils;

public class ConnectionUtils
{

    public static String tcp(int port)
    {
        return "tcp://*:" + port;
    }

    public static String ipc(String label)
    {
        return "ipc://" + label;
    }

    public static String inproc(String label)
    {
        return "inproc://" + label;
    }

    public static String tcp(String ip, int port)
    {
        if(ip == null || ip.isEmpty()){
            return  tcp(port);
        }
        return "tcp://" + ip + ":" + port;
    }

    private static String OS = null;
    private static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }
    public static boolean isWindows()
    {

        OS = System.getProperty("os.name");
        return getOsName().startsWith("Windows");
    }

}
