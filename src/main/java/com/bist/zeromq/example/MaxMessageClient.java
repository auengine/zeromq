package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.UUID;

public class MaxMessageClient
{
    private static final int serverLocalPort = Configuration.SERVER_COMMAND_PORT;
    private static final int serverDesPort =  Configuration.SERVER_STREAM_PORT;
    private static final String localServerIp = Configuration.SERVER_IP;
    private static final String destServerIp = Configuration.DEST_SERVER_IP;
    private static final byte[] answer = ByteBuffer.allocate(MaxMessageServer.MAX_ANW_BUFFER).array();
    private static final String instanceName = UUID.randomUUID().toString();
    private static final ByteBuffer answerBuf = ByteBuffer.allocate(MaxMessageServer.MAX_ANW_BUFFER);

    private static ReportWriter reportWriter;
    private static ZContext context;

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(MaxMessageClient.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n", MaxMessageClient.class.getSimpleName(), instanceName);
            reportWriter.printf("Connect: %s:%d via local  %s:%d \n", destServerIp,serverDesPort,localServerIp,serverLocalPort);
            context = new ZContext();

            final byte[] queryBuffer = ByteBuffer.allocate(MaxMessageServer.MAX_Q_BUFFER).array();

            for (int i = 0; i < queryBuffer.length; i++)
            {
                queryBuffer[i] = 1;
            }

            queryBuffer[0] = (byte)MaxMessageServer.qStart;
            queryBuffer[queryBuffer.length - 1] = (byte)MaxMessageServer.qEnd;


            ZFrame qFrame = new ZFrame(queryBuffer);


            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);


           // socket.bind("tcp://127.0.0.1:20103");
            socket.connect(ConnectionUtils.tcp(localServerIp,serverLocalPort,destServerIp,serverDesPort));

          //  socket.setHWM(1000);
            socket.setReceiveBufferSize(1024*1024*64);
            socket.setSendBufferSize(1024*1024);
           // socket.setLinger(1000);
          //  socket.setRcvHWM(1000);


            //MZFrame mzFrame = new MZFrame(answer);
            int i = 0;
            while (!Thread.currentThread().isInterrupted())
            {
                // String message = "Hi!" + instanceName +  " index :" + i;
                //reportWriter.println("Sending message: " + message);
                // socket.send(testBuffer,0,testBuffer.length,0);
                long start=System.nanoTime();
                //socket.send(queryBuffer,0,queryBuffer.length,0);
                qFrame.sendAndKeep(socket, 0);
                socket.recv(answer,0,answer.length,0);
                reportWriter.printf("Answered %d: %d\n", i, System.nanoTime()-start);
                i++;

                // ZFrame f= ZFrame.recvFrame(socket,0&128&);
                // f.destroy();
                //ZMsg answer=ZMsg.recvMsg(socket,0);
                //answer.destroy();
                //socket.recvByteBuffer(answerBuf,0);
                // answerBuf.clear();

                // MZFrame f= MZFrame.recvFrame(socket,0,mzFrame);
                // f.destroy();


                // int readSize= socket.recv(answer,0,answer.length, 0);
               /*
                // Block until a message is received
                ZMsg a=ZMsg.recvMsg(socket,0);
                if( a.contentSize() != MaxMessageServer.MAX_ANW_BUFFER){
                    reportWriter.println("Readed message not fit: " + a.contentSize());
                }
                zFrame=a.getFirst();
                if(zFrame.getData()[0] != (byte)MaxMessageServer.aStart
                    && (zFrame.getData()[zFrame.getData().length-1] != (byte) MaxMessageServer.aEnd )){
                    reportWriter.println("Incomplete answer! " );
                }
                zFrame.destroy();
                a.destroy();
                */


             /*
               int readSize= socket.recv(answer,0,answer.length, 0);
                if( readSize != MaxMessageServer.MAX_ANW_BUFFER){
                    reportWriter.println("Readed message not fit: " + readSize);
                }
                if(answer[0] != (byte)MaxMessageServer.aStart
                    && answer[answer.length-1] != (byte) MaxMessageServer.aEnd ){
                    reportWriter.println("Incomplete answer!" );
                }
             */
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

