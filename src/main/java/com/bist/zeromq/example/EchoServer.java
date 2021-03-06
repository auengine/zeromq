package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import com.bist.zeromq.utils.ConnectionUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

public class EchoServer
{
    private static final int serverPort = Configuration.SERVER_COMMAND_PORT;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName =   UUID.randomUUID().toString();

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(EchoServer.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  EchoServer.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind(ConnectionUtils.tcpOld(serverPort));

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

