package com.bist.zeromq;

import com.bist.zeromq.config.*;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.transfer.Request;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.service.RequestService;
import com.bist.zeromq.statistics.LatencyStatistics;
import com.bist.zeromq.statistics.Statistics;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClientProcess
{
    private static final int instanceId = Configuration.INSTANCE_ID;
    private static final String ipcOut = "ipc_c_out_" + instanceId;  //ClientProcess.class.getSimpleName();.
    private static final String serverIp = Configuration.SERVER_IP;
    private static final int peerCommandPort = Configuration.SERVER_COMMAND_PORT;
    private static final String instanceName = UUID.randomUUID().toString();
    private static final int messageTypeOpt = Configuration.MESSAGE_TYPE_ITEM;
    private static final int messageSizeOpt = Configuration.MESSAGE_SIZE_ITEM;
    private static final String waitTill = Configuration.WAIT_TIME_TILL;
    private static final Statistics statistics = new LatencyStatistics(10);
    private static final int numberOfMessages = Configuration.MESSAGE_COUNT;
    private static final int numberOfWarmUpMessages = 100;
    private static final byte[] answerBuffer = ByteBuffer.allocate(Constants.MAX_ANSWER_SIZE).array();
    private static final int totalClientCount = Configuration.TOTAL_CLIENT_COUNT;
    private static final int totalMessageTypeCount = Configuration.TOTAL_MESSAGE_TYPE_COUNT;
    private static ReportWriter reportWriter;
    private static ZContext context;
    private static ZMQ.Socket commandSocket;
    private static ZMQ.Socket ipcOutSocket;

    public static void main(String[] args)
    {
        try
        {
            List<MessageType> queryTypeList = Arrays.asList();
            List<MessageType> trtTypeList = Arrays.asList();
            MessageType messageType = MessageType.getByCode(messageTypeOpt);
            MessageSize messageSize = MessageSize.getByCode(messageSizeOpt);
            //no need to segmenting
            MessageSize segmentSize = messageSize;
            int segmentCount = messageSize.getSize() / segmentSize.getSize();

            reportWriter = GeneralUtils.createReportFile(ClientProcess.class.getSimpleName() + instanceName);
            reportWriter.printf("Starting %s with name %s\n", ClientProcess.class.getSimpleName(), instanceName);
            reportWriter.printf("Peer Port: %d, Ip: %s\n",  peerCommandPort,serverIp);
            String staticticPath = GeneralUtils
                .createBasePath(messageType, messageSize, totalClientCount, totalMessageTypeCount, instanceId);

            context = new ZContext();
            //exception handler
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            {
                public void uncaughtException(final Thread t, final Throwable e)
                {
                    reportWriter.println("Unknown exception. ");
                }
            });

            ProcessInfo processInfo = new ProcessInfo(instanceName, AppType.CLIENT, ipcOut, queryTypeList, trtTypeList);


            // Socket to talk to peer process command port
            reportWriter.println("Connecting peer command port");
            commandSocket = context.createSocket(SocketType.REQ);
            commandSocket.connect(ConnectionUtils.tcp(serverIp,peerCommandPort));
            commandSocket.send(CommandService.createCommand(processInfo, CommandCode.REGISTER_NEW_PROCESS_TO_PEER));

            //  - wait for  reply
            commandSocket.recv(0);

            reportWriter.printf("Starting sending on ipc %s\n", ipcOut);

            // Socket to talk to local peer process
            ipcOutSocket = context.createSocket(SocketType.REQ);
            ipcOutSocket.connect(ConnectionUtils.ipc(ipcOut));

            Request request = RequestService.createOrGet(messageType, messageSize);
            byte[] requestByteForm = request.encodedForm();
            ZMsg zQuery = new ZMsg();
            zQuery.add(requestByteForm);

            //warm up
            warmUp(zQuery, messageSize);
            reportWriter.println("Warmed up!");


            if(totalClientCount>1 && !waitTill.isEmpty() && waitTill.contains(":")){
                reportWriter.printf("Sleeping till %s!",waitTill);
                 String[] time= waitTill.split(":");
                 int tillTime= LocalTime.of(Integer.getInteger(time[0]),Integer.getInteger(time[1])).getNano();
                 int nanoDiff=tillTime-LocalTime.now().getNano();
                 Thread.sleep(nanoDiff/1000,nanoDiff%1000);
            }

            // Block until a message is received
            reportWriter.println("Starting sending queries!");

            for (int i = 0; i < numberOfMessages; i++)
            {
                long beginTime = System.nanoTime();
                for (int j = 0; j < segmentCount; j++)
                {
                    if (!zQuery.send(ipcOutSocket, false))
                    {
                        reportWriter.printf("Send failed %d %d!\n", i, j);
                        throw new IllegalStateException("Send failed!");
                    }
                    //ipcOutSocket.send(requestByteForm,0,requestByteForm.length,0);
                    int answerSize = ipcOutSocket.recv(answerBuffer, 0, segmentSize.getSize(), 0);
                    if (answerSize < 0 || answerSize != segmentSize.getSize())
                    {
                        reportWriter.printf("Segment mismatch %d expected %d \n", answerSize, segmentSize
                            .getSize());
                        throw new IllegalStateException("Send failed!");
                    }
                    //   reportWriter.printf("Segment response time %d \n", System.nanoTime() - beginTime);
                }
                long executionTime = System.nanoTime() - beginTime;
                //  reportWriter.printf("Request response time %d \n", executionTime);
                statistics.transactionCompleted(executionTime);
            }

            // statistics.getSummary();
            reportWriter.printf("Output to file: %s \n", staticticPath);
            statistics.dumpRawData(staticticPath);
            // byte[] reply = ipcOutSocket.recv(0);
            // String answer = new String(reply, ZMQ.CHARSET);
            // Print the message
            //reportWriter.println("Received: [" + message + "]");

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


    private static void warmUp(ZMsg zMsg, MessageSize messageSize) throws IOException
    {


        for (int i = 0; i < numberOfWarmUpMessages; i++)
        {
            //ipcOutSocket.send(request,0,request.length,0);
            if (!zMsg.send(ipcOutSocket, false))
            {
                reportWriter.printf("Send failed in warm %d!\n", i);
            }
            else
            {
                int answerSize = ipcOutSocket.recv(answerBuffer, 0, messageSize.getSize(), 0);
                if (answerSize != messageSize.getSize())
                {
                    reportWriter.printf("Segment mismatch %d expected %d \n",
                        answerSize, messageSize.getSize());
                }
            }


        }
    }
}


