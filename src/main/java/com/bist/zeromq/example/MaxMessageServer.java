package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MaxMessageServer
{
    private static final int serverPort =20104;
    public static int MAX_BUFFER=1024*1024*50 *10;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName =   UUID.randomUUID().toString();
    private static final byte[] testBuffer = ByteBuffer.allocate(MAX_BUFFER).array();
    private static final byte[] answer = ByteBuffer.allocate(MaxMessageServer.MAX_BUFFER).array();

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(MaxMessageServer.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  MaxMessageServer.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();

            answer[0]='S';
            answer[1]='T';
            answer[answer.length-1]='!';
            answer[answer.length-2]='D';
            answer[answer.length-3]='N';

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind(ConnectionUtils.tcp(serverPort));

            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling recv ");
                int size = socket.recv(testBuffer,0,testBuffer.length, 0);
               // String message = new String(testBuffer, ZMQ.CHARSET);
                // Print the message
                reportWriter.println("Received: [" + size + "]");

                // Send a response
                socket.send(answer,0,answer.length,0 );
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

