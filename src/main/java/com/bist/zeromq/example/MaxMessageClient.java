package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class MaxMessageClient
{
    private static final int serverPort = 20104;
    private static final String serverIp = Configuration.SERVER_IP;
    private static String instanceName =   UUID.randomUUID().toString();
    private static final byte[] testBuffer = ByteBuffer.allocate(MaxMessageServer.MAX_BUFFER/2).array();
    private static final byte[] answer = ByteBuffer.allocate(MaxMessageServer.MAX_BUFFER/2).array();

    private static ReportWriter reportWriter;
    private static ZContext context;

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(MaxMessageClient.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  MaxMessageClient.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();

            for (int i =0; i< testBuffer.length;i++)
            {
                testBuffer[i]=1;
            }

            testBuffer[0]='S';
            testBuffer[1]='T';
            testBuffer[testBuffer.length-1]='!';
            testBuffer[testBuffer.length-2]='D';
            testBuffer[testBuffer.length-3]='N';

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect(ConnectionUtils.tcp(serverIp, serverPort));

            int i = 0;
            while (!Thread.currentThread().isInterrupted())
            {
               // String message = "Hi!" + instanceName +  " index :" + i;
                //reportWriter.println("Sending message: " + message);
                socket.send(testBuffer,0,testBuffer.length,0);
                i++;
                // Block until a message is received
                int size=socket.recv(answer,0,answer.length, 0);

                reportWriter.println("Readed message: " + size);

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

