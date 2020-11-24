package com.bist.zeromq;

import com.bist.zeromq.config.*;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.transfer.Request;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import com.bist.zeromq.utils.ConnectionUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class ServerProcess
{
    private static  int peerCommandPort = Configuration.SERVER_COMMAND_PORT;
    private static final int instanceId = Configuration.INSTANCE_ID;
    private static final List<Integer> queryList = Configuration.getSortedProperties(
        Configuration.QEUERY_LIST);
    private static final List<Integer> trtList = Configuration.getSortedProperties(
        Configuration.TRT_LIST);
    private static final String ipcIn = "ipc_s_in_" +instanceId ; //+ ServerProcess.class.getSimpleName();
   // private static final String ipcOut = "out_" + ServerProcess.class.getSimpleName();

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName = UUID.randomUUID().toString();
    private static ZMQ.Socket ipcInSocket;
    private static ZMQ.Socket commandSocket;
    private static final List<MessageType> queryTypeList= MessageType.asEnum(queryList);
    private static final List<MessageType> trtTypeList= MessageType.asEnum(trtList);

    private static final byte[] queryAndTrtBuffer = ByteBuffer.allocate(Constants.MAX_ANSWER_SIZE).array();


    public static void main(String[] args)
    {
        try
        {

            reportWriter = GeneralUtils.createReportFile(ServerProcess.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  ServerProcess.class.getSimpleName(),instanceName);
            reportWriter.printf("Peer Port: %d\n",  peerCommandPort);
            context = new ZContext();

            ProcessInfo processInfo = new ProcessInfo(instanceName, AppType.SERVER,ipcIn,queryTypeList,trtTypeList);


            // Socket to talk to peer process command port
            reportWriter.println("Connecting peer command port");
            commandSocket = context.createSocket(SocketType.REQ);
            commandSocket.connect(ConnectionUtils.tcp(peerCommandPort));
            String base64Command= CommandService.createCommand(processInfo, CommandCode.REGISTER_NEW_PROCESS_TO_PEER);
            commandSocket.send(base64Command);
            //  wait for  reply
            commandSocket.recv(0);

            // Socket to talk to local peer process
            ipcInSocket = context.createSocket(SocketType.REP);
            ipcInSocket.bind(ConnectionUtils.ipc(ipcIn));
            reportWriter.printf("Starting listening on ipc %s\n", ipcIn);

            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
               // reportWriter.println("Waiting for request!");
                int querySize = ipcInSocket.recv(queryAndTrtBuffer,0,Request.getByteSize(),0 );
                Request request=Request.decodedForm(queryAndTrtBuffer);
             //   reportWriter.printf("Request is %s answer size %s!\n",request.toString(),request.getRequestedAnswerSize().getSize());
                ipcInSocket.send(AnswerService.getAnswer(),0,request.getRequestedAnswerSize().getSize(), 0);
               // reportWriter.println("Requested answered!");
            }
        }
        catch (Exception e)
        {
            reportWriter.println("Exception occured:" + e.getLocalizedMessage());
        }
        finally
        {

            if(commandSocket != null) commandSocket.close();
            if(ipcInSocket != null) ipcInSocket.close();

            if (context != null)
            {
                context.close();
            }
            reportWriter.println("Finalizing application.");
        }
    }
}

