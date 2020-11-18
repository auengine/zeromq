package com.bist.zeromq.handler;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.ZeroPeerRoutingInfo;
import com.bist.zeromq.model.internal.PeerProcessInfo;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.internal.RoutingTable;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;


public class PeerCommandHandler extends Thread
{
    private final String inProcPath;
    private final ZeroPeerRoutingInfo zeroPeerRoutingInfo;
    private final ReportWriter reportWriter;
    private final ZContext context;
    private final String publisherIp;
    private final int publisherServerPort;
    private final int publisherSubscribePort;

    private ZMQ.Socket inProcSocket;
    private ZMQ.Socket publisherServerSocket;
    private ZMQ.Socket subscriberSocket;
    private ZMQ.Poller poller;

    public PeerCommandHandler(ZContext context, String publisherIp, int publisherServerPort,
        int publisherSubscribePort, String inProcPath, ZeroPeerRoutingInfo zeroPeerRoutingInfo,
        ReportWriter reportWriter)
    {
        this.context = context;
        this.zeroPeerRoutingInfo = zeroPeerRoutingInfo;
        this.reportWriter = reportWriter;
        this.inProcPath = inProcPath;
        this.publisherIp = publisherIp;
        this.publisherServerPort = publisherServerPort;
        this.publisherSubscribePort = publisherSubscribePort;
    }


    @Override
    public void run()
    {
        try
        {
            reportWriter.printf("Thread started with inProcPath %s \n", this.inProcPath);
            inProcSocket = context.createSocket(SocketType.PAIR);
            inProcSocket.bind(ConnectionUtils.inproc(inProcPath));

            //  First, connect our subscriber socket
            subscriberSocket = context.createSocket(SocketType.SUB);
            subscriberSocket.connect(ConnectionUtils.tcp(publisherIp, publisherSubscribePort));
            subscriberSocket.subscribe(ZMQ.SUBSCRIPTION_ALL);

            //  register to publisher
            publisherServerSocket = context.createSocket(SocketType.REQ);
            publisherServerSocket.connect(ConnectionUtils.tcp(publisherIp, publisherServerPort));
           // reportWriter.printf("Publisher:%s\n", new String(publisherReply, ZMQ.CHARSET));

            // thread is ok
            inProcSocket.send(AnswerService.getOKMessage());

            //  Initialize poll set
            poller = context.createPoller(2);
            poller.register(inProcSocket, ZMQ.Poller.POLLIN);
            poller.register(subscriberSocket, ZMQ.Poller.POLLIN);

            reportWriter.println("Starting pooling in command handler!");
            while (!Thread.currentThread().isInterrupted())
            {
                byte[] message = null;
                // Block until a message is received
                poller.poll();
                reportWriter.println("Items pooled!");
                //server socket
                if (poller.pollin(0))
                {
                    message = inProcSocket.recv(0);
                    inProcSocket.send(AnswerService.getOKMessage());
                }
                //subscriber
                if (poller.pollin(1))
                {
                    message = subscriberSocket.recv(0);
                }
                handleCommand(message);

            }

        }
        catch (Exception e)
        {
            reportWriter.println("Thread exception" + e.getLocalizedMessage());
        }
        finally
        {
            if (this.publisherServerSocket != null)
            {
                this.publisherServerSocket.close();
            }
            if (this.inProcSocket != null)
            {
                this.inProcSocket.close();
            }
            if (this.subscriberSocket != null)
            {
                this.subscriberSocket.close();
            }
            if (this.poller != null)
            {
                this.poller.close();
            }
            //this.join();

        }
    }

    public void handleCommand(byte[] message) throws IOException
    {

        // reportWriter.printf("Subscriber received: %s", message);
        if (message == null)
        {
            reportWriter.errorln("message is null!");
        }
        Command command = CommandService.base642Command(message);

        reportWriter.printf("Command handler: %s!\n",command.getCommandCode());
        if (command.getCommandCode() == CommandCode.NEW_SERVER_REGISTERED_TO_TRACKER)
        {
            RoutingTable newRoutingTable = (RoutingTable)command.getData();
            zeroPeerRoutingInfo.update(newRoutingTable);
            reportWriter.println(zeroPeerRoutingInfo.getRoutingTable().buildTableStr());
            reportWriter.println("New server registered ack!");
        }else if (command.getCommandCode() == CommandCode.NEW_CLIENT_REGISTERED_TO_TRACKER)
        {
            RoutingTable newRoutingTable = (RoutingTable)command.getData();
            zeroPeerRoutingInfo.update(newRoutingTable);
            reportWriter.println(zeroPeerRoutingInfo.getRoutingTable().buildTableStr());
            reportWriter.println("New client registered ack!");
            // start pooling
        }
        //from process
        else if (command.getCommandCode() == CommandCode.REGISTER_NEW_PROCESS_TO_PEER)
        {

            ProcessInfo processInfo = (ProcessInfo)command.getData();
            PeerProcessInfo peerProcessInfo =
                new PeerProcessInfo(zeroPeerRoutingInfo.getCurrentPeerInfo(), processInfo);
            publisherServerSocket.send(CommandService
                   .createCommand(peerProcessInfo, CommandCode.REGISTER_NEW_PROCESS_TO_TRACKER));
            publisherServerSocket.recv(0);
            reportWriter.println("Publisher ack!");

        }
    }

}
