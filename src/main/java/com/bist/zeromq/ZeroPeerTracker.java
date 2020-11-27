package com.bist.zeromq;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.handler.TrackerCommandHandler;
import com.bist.zeromq.model.ZeroTrackerRoutingInfo;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

public class ZeroPeerTracker
{
    private static final int serverCommandPort = Configuration.PUBLISHER_COMMAND_PORT;
    private static final int publisherPort = Configuration.PUBLISHER_SUBSCRIBE_PORT;
    private static final String serverIp = Configuration.SERVER_IP;
    private static final String inProcLabel1 = "inproc_t_command_1";
    private static ZMQ.Socket inProcSocket;
    private static ZMQ.Socket commandSocket;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static final String instanceName = UUID.randomUUID().toString();
    private static final ZeroTrackerRoutingInfo zeroTrackerRoutingInfo = new ZeroTrackerRoutingInfo(instanceName, serverIp, serverCommandPort, publisherPort);
    private static Thread commandHandler;

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(ZeroPeerTracker.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n", ZeroPeerTracker.class.getSimpleName(),
                instanceName);
            reportWriter.printf("Port: %d, publisherPort: %d\n", serverCommandPort, publisherPort);
            context = new ZContext();


            // Socket to talk to peers
            commandSocket = context.createSocket(SocketType.REP);
            commandSocket.bind(ConnectionUtils.tcp(serverIp,serverCommandPort));

            // Thread  socket
            inProcSocket = context.createSocket(SocketType.PAIR);
            inProcSocket.connect(ConnectionUtils.inproc(inProcLabel1));
            commandHandler = new TrackerCommandHandler(context, inProcLabel1, zeroTrackerRoutingInfo, reportWriter);
            commandHandler.start();

            // wait for thread for ok!
            inProcSocket.recv();

            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                reportWriter.println("Calling recv ");
                byte[] message = commandSocket.recv(0);
                // String message = new String(reply, ZMQ.CHARSET);
                // Print the message
                reportWriter.println("Tracker server port received!.");
                //detach to thread
                inProcSocket.send(message);
                commandSocket.send(AnswerService.getOKMessage());

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
            if (inProcSocket != null)
            {
                inProcSocket.close();
            }
            if (commandHandler != null)
            {
                commandHandler.interrupt();
            }
            if (context != null)
            {
                context.close();
            }
            reportWriter.println("Finalizing application.");
        }
    }

}

