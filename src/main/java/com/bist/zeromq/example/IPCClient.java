package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

public class IPCClient
{
   // private static final int serverPort = Configuration.SERVER_PORT;
   // private static final String serverIp = Configuration.SERVER_IP;
    private static String ipcLabel ="test";
    private static String instanceName =   UUID.randomUUID().toString();


    private static ReportWriter reportWriter;
    private static ZContext context;

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(IPCClient.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  IPCClient.class.getSimpleName(),instanceName);
            reportWriter.printf("Ipc: %s\n",  ipcLabel);
            context = new ZContext();

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect(ConnectionUtils.ipc(ipcLabel));

            int i = 0;
            while (!Thread.currentThread().isInterrupted())
            {
                String message = "Hi!" + instanceName +  " index :" + i;
                reportWriter.println("Sending message: " + message);
                socket.send(message);
                i++;
                // Block until a message is received
                byte[] reply = socket.recv();
                String ack = new String(reply, ZMQ.CHARSET);
                reportWriter.println("Readed message: " + ack);

            }
        }
        catch (Exception e)
        {
            reportWriter.println("Exception occured:" + e.getLocalizedMessage());
        }
        finally
        {
            if (context != null)
            {
                context.close();
            }
            reportWriter.println("Finalizing application.");
        }
    }

}

