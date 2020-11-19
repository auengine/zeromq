package com.bist.zeromq.handler;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.ZeroPeerRoutingInfo;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.model.transfer.Query;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Optional;


public class PeerStreamHandler extends Thread
{
    private final String inProcPath;
    private final String inProcThreadPath;
    private final ZeroPeerRoutingInfo zeroPeerRoutingInfo;
    private final ReportWriter reportWriter;
    private final ZContext context;
    private ZMQ.Socket inProcSocket;
    private ZMQ.Socket inProcThreadSocket;
    private ZMQ.Poller poller;


    public PeerStreamHandler(ZContext context, String inProcPath, String inProcThreadPath, ZeroPeerRoutingInfo zeroPeerRoutingInfo,
        ReportWriter reportWriter)
    {
        this.context = context;
        this.zeroPeerRoutingInfo = zeroPeerRoutingInfo;
        this.reportWriter = reportWriter;
        this.inProcPath = inProcPath;
        this.inProcThreadPath = inProcThreadPath;
    }


    @Override
    public void run()
    {
        try
        {
            reportWriter.printf("Thread started with inProcPath %s \n", this.inProcPath);
            //for peer stream
            inProcSocket = context.createSocket(SocketType.PAIR);
            inProcSocket.bind(ConnectionUtils.inproc(inProcPath));
            //for thread commands
            inProcThreadSocket = context.createSocket(SocketType.PAIR);
            inProcThreadSocket.bind(ConnectionUtils.inproc(inProcThreadPath));

            //notify main thread as ready
            inProcSocket.send(AnswerService.getOKMessage());

            //  Initialize poll set
            poller = context.createPoller(10);
            poller.register(inProcSocket, ZMQ.Poller.POLLIN);
            poller.register(inProcThreadSocket, ZMQ.Poller.POLLIN);


            reportWriter.println("Starting receiving in stream handler!");
            int pCount = 1;
            while (!Thread.currentThread().isInterrupted())
            {
                // Block until a message is received
                poller.poll();
                reportWriter.println("Items pooled in stream thread!");
                //pear stream socket
                if (poller.pollin(0))
                {
                    reportWriter.println("Handling stream reguest for peer!");
                    byte[] stream = inProcSocket.recv(0);
                  //  inProcSocket.send(AnswerService.getOKMessage());
                    handleStream(stream,inProcSocket);

                }
                //command thread
                if (poller.pollin(1))
                {

                    byte[] base64command = inProcThreadSocket.recv(0);
                    Command command = CommandService.base642Command(base64command);
                    reportWriter.printf("Stream thread notified with command %s \n",command.getCommandCode());
                    //
                    if (command.getCommandCode() == CommandCode.NEW_CLIENT_REGISTERED_TO_TRACKER)
                    {
                        Optional<ZMQ.Socket> newClient = zeroPeerRoutingInfo.getNewClientSocket(context);
                        if (newClient.isPresent())
                        {
                            poller.register(newClient.get(), ZMQ.Poller.POLLIN);
                            pCount++;
                            reportWriter.printf("New client registered to stream thread: %d!\n",pCount);
                        }
                    }
                    inProcThreadSocket.send(AnswerService.getOKMessage());
                }
                for (int i = 2; i <= pCount; i++)
                {
                    reportWriter.println("Checking  client streams!");
                    if (poller.pollin(i))
                    {
                        ZMQ.Socket client = poller.getSocket(i);
                        byte[] stream = client.recv(0);
                        reportWriter.printf("Handling stream reguest for client %d!\n", i);
                        handleStream(stream,client);


                    }

                }

            }

        }
        catch (Exception e)
        {
            reportWriter.println("Thread exception" + e.getLocalizedMessage());
        }
        finally
        {

            if (this.inProcSocket != null)
            {
                this.inProcSocket.close();
            }
            if (this.inProcThreadSocket != null)
            {
                this.inProcThreadSocket.close();
            }
            //this.join();

        }
    }

    public void handleStream(byte[] stream, ZMQ.Socket out)
    {
        Query query = Query.decodedForm(stream);
        reportWriter.println("Decoded query is" + query.toString());
        ZMQ.Socket socket = zeroPeerRoutingInfo.getStreamSocket(query, context);
        reportWriter.println("Directing stream to destination.");
        socket.send(stream);
        byte[] reply = socket.recv();
        reportWriter.println("Directing stream to answered.");
        out.send(reply);
        reportWriter.println("Stream directed to main thread");


    }

}
