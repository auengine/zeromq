package com.bist.zeromq;

import com.bist.zeromq.config.*;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.IDataUtil;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClientProcess
{
    private static final int peerServerPort = Configuration.SERVER_COMMAND_PORT;


    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName = UUID.randomUUID().toString();
    private static final String ipcOut = "out_" + instanceName;
    private static MessageSize messageSize=MessageSize.KB60;
    private static  ZMQ.Socket commandSocket;
    private static  ZMQ.Socket outSocket;

    public static void main(String[] args)
    {
        try
        {
            List<QueryType> queryTypeList=  Arrays.asList();
            List<TrtType>   trtTypeList=  Arrays.asList();

            reportWriter = GeneralUtils.createReportFile(ClientProcess.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  ClientProcess.class.getSimpleName(),instanceName);
            reportWriter.printf("Peer Port: %d\n",  peerServerPort);
            context = new ZContext();

            ProcessInfo processInfo = new ProcessInfo(instanceName,AppType.CLIENT,ipcOut,queryTypeList,trtTypeList);


            // Socket to talk to peer process command port
            commandSocket = context.createSocket(SocketType.REQ);
            commandSocket.connect(ConnectionUtils.tcp(peerServerPort));
            commandSocket.send(CommandService.createCommand(processInfo, CommandCode.REGISTER_NEW_PROCESS_TO_PEER));

            //  - wait for  reply
            commandSocket.recv(0);

            // Socket to talk to local peer process
            outSocket = context.createSocket(SocketType.REQ);
            outSocket.connect(ConnectionUtils.ipc(ipcOut));


            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling send ");
                outSocket.send(AnswerService.getJunkMessage(messageSize));
                byte[] reply = outSocket.recv(0);
                String answer = new String(reply, ZMQ.CHARSET);
                // Print the message
                //reportWriter.println("Received: [" + message + "]");

                reportWriter.println("Message acknowledged.");
            }
        }
        catch (Exception e)
        {
            reportWriter.println("Exception occured:" + e.getLocalizedMessage());
        }
        finally
        {
            if(commandSocket!=null) commandSocket.close();
            if(outSocket!=null) outSocket.close();
            if (context != null) context.close();
            reportWriter.println("Finalizing application.");
        }
    }
}

