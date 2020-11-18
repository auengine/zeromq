package com.bist.zeromq;

import com.bist.zeromq.config.*;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.service.QueryService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClientProcess
{
    private static final int instanceId = Configuration.INSTANCE_ID;
    private static final String ipcOut = "out_" + instanceId;  //ClientProcess.class.getSimpleName();.
    private static final int peerCommandPort = 20124;// Configuration.SERVER_COMMAND_PORT;
    private static ReportWriter reportWriter;
    private static ZContext context;
    private static final String instanceName = UUID.randomUUID().toString();
    private static final MessageSize messageSize = MessageSize.MB30;
    private static final QueryType queryType = QueryType.TYPE0 ;
    private static ZMQ.Socket commandSocket;
    private static ZMQ.Socket ipcOutSocket;

    public static void main(String[] args)
    {
        try
        {
            List<QueryType> queryTypeList = Arrays.asList();
            List<TrtType> trtTypeList = Arrays.asList();

            reportWriter = GeneralUtils.createReportFile(ClientProcess.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n", ClientProcess.class.getSimpleName(), instanceName);
            reportWriter.printf("Peer Port: %d\n", peerCommandPort);
            context = new ZContext();

            ProcessInfo processInfo = new ProcessInfo(instanceName, AppType.CLIENT, ipcOut, queryTypeList, trtTypeList);


            // Socket to talk to peer process command port
            reportWriter.println("Connecting peer command port");
            commandSocket = context.createSocket(SocketType.REQ);
            commandSocket.connect(ConnectionUtils.tcp(peerCommandPort));
            commandSocket.send(CommandService.createCommand(processInfo, CommandCode.REGISTER_NEW_PROCESS_TO_PEER));

            //  - wait for  reply
            commandSocket.recv(0);
            reportWriter.printf("Starting sending on ipc %s", ipcOut);

            // Socket to talk to local peer process
            ipcOutSocket = context.createSocket(SocketType.REQ);
            ipcOutSocket.connect(ConnectionUtils.ipc(ipcOut));


            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling send ");
                ipcOutSocket.send(QueryService.createQuery(queryType,messageSize).encodedForm());
                byte[] reply = ipcOutSocket.recv(0);
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


            if (commandSocket != null)
            {
                commandSocket.close();
            }
            if (ipcOutSocket != null)
            {
                ipcOutSocket.close();
            }

            if (context != null)
            {
                context.close();
            }
            reportWriter.println("Finalizing application.");
        }

    }
}

