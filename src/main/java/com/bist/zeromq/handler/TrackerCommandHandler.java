package com.bist.zeromq.handler;

import com.bist.zeromq.config.AppType;
import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.ZeroTrackerRoutingInfo;
import com.bist.zeromq.model.internal.PeerProcessInfo;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.model.transfer.IMessage;
import com.bist.zeromq.model.internal.IInternalInfo;
import com.bist.zeromq.model.internal.PeerInfo;
import com.bist.zeromq.model.internal.RoutingTable;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.IDataUtil;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class TrackerCommandHandler extends Thread
{
    private final String socketLabel;
    private ZeroTrackerRoutingInfo zeroTrackerRoutingInfo;
    private final ReportWriter reportWriter;
    private final ZContext context;
    private ZMQ.Socket inProcSocket;
    private ZMQ.Socket publisherSocket;


    public TrackerCommandHandler(ZContext context, String socketLabel,
        ZeroTrackerRoutingInfo zeroTrackerRoutingInfo, ReportWriter reportWriter)
    {
        this.socketLabel = socketLabel;
        this.reportWriter = reportWriter;
        this.context = context;
        this.zeroTrackerRoutingInfo = zeroTrackerRoutingInfo;
    }

    @Override
    public void run()
    {
        try
        {
            reportWriter.printf("Thread started with socketLabel %s \n", this.socketLabel);

            inProcSocket = context.createSocket(SocketType.PAIR);
            inProcSocket.bind(ConnectionUtils.inproc(socketLabel));

             //bind
            publisherSocket = context.createSocket(SocketType.PUB);
            publisherSocket.bind(
                ConnectionUtils.tcp(zeroTrackerRoutingInfo.getCurrentTrackerInfo().getIp(),zeroTrackerRoutingInfo.getCurrentTrackerInfo().getPublisherPort()));

            inProcSocket.send(AnswerService.getOKMessage());

            while (true)
            {
                // Block until a message is received
                reportWriter.println("Calling recv on tracker command handler ");
                byte[] message = inProcSocket.recv(0);
               // String message = new String(reply, ZMQ.CHARSET);
                // Print the message
                reportWriter.println("Received new command on tracker command handler");

                Command command = CommandService.base642Command(message);
                if (command.getCommandCode() == CommandCode.REGISTER_NEW_PROCESS_TO_TRACKER)
                {
                    PeerProcessInfo peerProcessInfo = (PeerProcessInfo)command.getData();
                    CommandCode commandCode= peerProcessInfo.getProcessInfo().getType() == AppType.SERVER ?
                        CommandCode.NEW_SERVER_REGISTERED_TO_TRACKER : CommandCode.NEW_CLIENT_REGISTERED_TO_TRACKER;
                    zeroTrackerRoutingInfo.registerNewProcess(peerProcessInfo);
                    String encodedMessage = CommandService.createCommand(zeroTrackerRoutingInfo.getRoutingTable(),commandCode);
                    reportWriter.println(zeroTrackerRoutingInfo.getRoutingTable().buildTableStr());
                    publisherSocket.send(encodedMessage);

                }
                reportWriter.println("Publish tracker...");
            }

        }
        catch (Exception e)
        {
            reportWriter.println("Thread exception" + e.getLocalizedMessage());
        }
        finally
        {
            if (this.publisherSocket != null)
            {
                this.publisherSocket.close();
            }
            if (this.inProcSocket != null)
            {
                this.inProcSocket.close();
            }
            //this.join();

        }
    }

}



