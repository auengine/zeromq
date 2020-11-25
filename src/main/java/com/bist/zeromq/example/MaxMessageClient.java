package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.*;
import zmq.Msg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class MaxMessageClient
{
    private static final int serverPort = 20104;
    private static final String serverIp = Configuration.SERVER_IP;
    private static String instanceName =   UUID.randomUUID().toString();

    private static final byte[] answer = ByteBuffer.allocate(MaxMessageServer.MAX_ANW_BUFFER).array();

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

           final byte[] queryBuffer = ByteBuffer.allocate(MaxMessageServer.MAX_Q_BUFFER).array();

            for (int i =0; i< queryBuffer.length;i++)
            {
                queryBuffer[i]=1;
            }

            queryBuffer[0]=(byte)MaxMessageServer.qStart;
            queryBuffer[queryBuffer.length-1]=(byte)MaxMessageServer.qEnd;


            Msg msq= new Msg();
            ZFrame frame =new ZFrame(queryBuffer);
            ZMsg queryZ= new ZMsg();
            queryZ.add(frame);


            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect(ConnectionUtils.tcp(serverIp, serverPort));

            ZFrame zFrame=null;
            int i = 0;
            while (!Thread.currentThread().isInterrupted())
            {
               // String message = "Hi!" + instanceName +  " index :" + i;
                //reportWriter.println("Sending message: " + message);
               // socket.send(testBuffer,0,testBuffer.length,0);
               if(!queryZ.send(socket,false)){
                   reportWriter.println("Query failed i: " + i);
               }
                i++;
                // Block until a message is received
         /*        ZMsg a=ZMsg.recvMsg(socket,0);
                if( a.contentSize() != MaxMessageServer.MAX_ANW_BUFFER){
                    reportWriter.println("Readed message not fit: " + a.contentSize());
                }
                zFrame=a.getFirst();
                if(zFrame.getData()[0] != (byte)MaxMessageServer.aStart
                    && (zFrame.getData()[zFrame.getData().length-1] != (byte) MaxMessageServer.aEnd )){
                    reportWriter.println("Incomplete answer! " );
                }
                zFrame.destroy();
                a.destroy(); */


               int readSize= socket.recv(answer,0,answer.length, 0);
                if( readSize != MaxMessageServer.MAX_ANW_BUFFER){
                    reportWriter.println("Readed message not fit: " + readSize);
                }
                if(answer[0] != (byte)MaxMessageServer.aStart
                    && answer[answer.length-1] != (byte) MaxMessageServer.aEnd ){
                    reportWriter.println("Incomplete answer!" );
                }

             //   reportWriter.println("Readed message!");

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

