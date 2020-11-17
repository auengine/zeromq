package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

public class IPCServer
{
   // private static final int serverPort = Configuration.SERVER_PORT;

    private static String ipcLabel ="test";

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static final String instanceName =   UUID.randomUUID().toString();

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(IPCServer.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  IPCServer.class.getSimpleName(),instanceName);
            reportWriter.printf("Ipc: %s\n",  ipcLabel);
            context = new ZContext();

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind(ConnectionUtils.ipc(ipcLabel));

            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling recv ");
                byte[] reply = socket.recv(0);
                String message = new String(reply, ZMQ.CHARSET);
                // Print the message
                reportWriter.println("Received: [" + message + "]");


                // Send a response
                String response = "Echo:" + message;
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
                reportWriter.println("Response send");
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

