package com.bist.zeromq.utils;

public class ConnectionUtils
{

    public static String tcpOld(int port)
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

        return "tcp://" + ip + ":" + port;
    }


    public static String tcp(String localIp, int localPort,String destIP, int destPort)
    {

        return "tcp://" + localIp + ":" + localPort + ";"+ destIP+ ":" + destPort;
    }


}
