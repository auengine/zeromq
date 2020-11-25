package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.*;
import zmq.Msg;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MaxMessageServer
{
    private static final int serverPort =20104;
    public static int MAX_ANW_BUFFER=1024*1024*50;
    public static int MAX_Q_BUFFER=1024*1024*1;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName =   UUID.randomUUID().toString();
    private static final byte[] queryBuffer = ByteBuffer.allocate(MAX_Q_BUFFER).array();
    public static char qStart='Q';
    public static char qEnd='!';
    public static char aStart='A';
    public static char aEnd='?';


    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(MaxMessageServer.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  MaxMessageServer.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();
            final byte[] answer = ByteBuffer.allocate(MaxMessageServer.MAX_ANW_BUFFER).array();
             for (int i =0; i<answer.length;i++){
                answer[i]=1;
             }


            answer[0]=(byte)aStart;
            answer[answer.length-1]=(byte)aEnd;


            Msg msq= new Msg();
            ZFrame frame =new ZFrame(answer);
            ZMsg answerZ= new ZMsg();
            answerZ.add(frame);


            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind(ConnectionUtils.tcp(serverPort));

            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling recv ");

              //  ZMsg inputMsg= ZMsg.recvMsg(socket,0);
                // inputMsg.destroy();

                int size = socket.recv(queryBuffer,0,queryBuffer.length, 0);
                if(size!=MAX_Q_BUFFER){
                    reportWriter.println("Readed message not fit: " + size);
                }
                if(queryBuffer[0] != (byte)qStart
                    && queryBuffer[size-1] != (byte) qEnd ){
                    reportWriter.println("Incomplete query " );
                }

               // String message = new String(testBuffer, ZMQ.CHARSET);
                // Print the message
             //   reportWriter.println("Received: [" + inputMsg.size() +"-"+inputMsg.contentSize() + "]");

                // Send a response
               // socket.send(answer,0,answer.length,0 );
                answerZ.send(socket,false);
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

