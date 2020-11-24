package com.bist.zeromq;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.handler.PeerCommandHandler;
import com.bist.zeromq.handler.PeerStreamHandler;
import com.bist.zeromq.model.ZeroPeerRoutingInfo;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.UUID;

public class ZeroPeer
{
    private static  int serverCommandPort =  Configuration.SERVER_COMMAND_PORT;
    private static  int serverStreamPort = Configuration.SERVER_STREAM_PORT;
    private static final String serverIp = Configuration.SERVER_IP;
    private static final int publisherSubscribePort = Configuration.PUBLISHER_SUBSCRIBE_PORT;
    private static final String publisherIp = Configuration.PUBLISHER_IP;
    private static final int publisherCommandPort = Configuration.PUBLISHER_COMMAND_PORT;
    private static final String inProcCommandPath = "inproc_p_command_1";
    private static final String inProcStreamPath = "inproc_p_stream_1";
    private static final String inProcStreamThreadPath = "inproc_p_stream_2";
    private static final String instanceName = UUID.randomUUID().toString();
    private static final ZeroPeerRoutingInfo zeroPeerRoutingInfo = new ZeroPeerRoutingInfo(instanceName,serverIp,serverCommandPort,serverStreamPort);
    private static ReportWriter reportWriter;
    private static ZContext context;
    private static ZMQ.Socket commandSocket;
    private static ZMQ.Socket streamSocket;
    private static ZMQ.Socket commandInprocSocket;
    private static ZMQ.Socket streamInprocSocket;
    private static ZMQ.Poller poller;
    private static Thread commandHandler;
    private static Thread streamHandler;



    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(ZeroPeer.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n", ZeroPeer.class.getSimpleName(), instanceName);
            reportWriter
                .printf("Port: %d, publisherIp: %s, publisherSubscribePort: %d \n", serverCommandPort, publisherIp, publisherSubscribePort);
            context = new ZContext();

            // Socket for commands
            commandSocket = context.createSocket(SocketType.REP);
            commandSocket.bind(ConnectionUtils.tcp(serverCommandPort));

            //Socket for message stream
            streamSocket = context.createSocket(SocketType.REP);
            streamSocket.bind(ConnectionUtils.tcp(serverIp,serverStreamPort));

             commandHandler = new PeerCommandHandler(context, publisherIp, publisherCommandPort,
                publisherSubscribePort, inProcCommandPath,inProcStreamThreadPath,
                zeroPeerRoutingInfo, reportWriter);

            streamHandler = new PeerStreamHandler(context, inProcStreamPath,inProcStreamThreadPath,
                zeroPeerRoutingInfo, reportWriter);

            //order important
            streamHandler.start();
            commandHandler.start();

            // Thread handler sockets
            commandInprocSocket = context.createSocket(SocketType.PAIR);
            commandInprocSocket.connect(ConnectionUtils.inproc(inProcCommandPath));
            streamInprocSocket = context.createSocket(SocketType.PAIR);
            streamInprocSocket.connect(ConnectionUtils.inproc(inProcStreamPath));

            //wait for thread for ok!
            streamInprocSocket.recv();
            commandInprocSocket.recv();


            //  Initialize poll set
            poller = context.createPoller(2);
            poller.register(commandSocket, ZMQ.Poller.POLLIN);
            poller.register(streamSocket, ZMQ.Poller.POLLIN);
            reportWriter.println("Starting pooling in  peer.");

            while (!Thread.currentThread().isInterrupted())
            {

                // Block until a message is received
                poller.poll();
               // reportWriter.println("Items pooled in peer!");
                //server socket
                if (poller.pollin(0))
                {
                    byte[] message = commandSocket.recv(0);
                    reportWriter.println("Command socket received!");
                    commandInprocSocket.send(message);
                    //wait for thread for ok!
                    commandInprocSocket.recv();
                    commandSocket.send(AnswerService.getOKMessage());
                    reportWriter.println("Command processed!");
                }
                //stream
                if (poller.pollin(1))
                {
                    byte[] message = streamSocket.recv(0);
                  //  reportWriter.println("Stream socket received");
                    streamInprocSocket.send(message);
                    //wait for thread for ok!
                    byte[] reply= streamInprocSocket.recv();
                    streamSocket.send(reply);
              //      reportWriter.println("Stream directed to stream socket.!");
                }
            }
        }
        catch (Exception e)
        {
            reportWriter.println("Exception occured:" + e.getLocalizedMessage());
        }
        finally
        {

            if(streamSocket != null) streamSocket.close();
            if(commandSocket != null) commandSocket.close();
            if(streamInprocSocket != null) streamInprocSocket.close();
            if(commandInprocSocket != null) commandInprocSocket.close();

            //check
            if(streamHandler != null) streamHandler.interrupt();
            if(commandHandler != null) commandHandler.interrupt();
            if (poller != null)  poller.close();
            if (context != null)  context.close();

            reportWriter.println("Finalizing application.");
        }
    }
}

