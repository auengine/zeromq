package com.bist.zeromq.handler;

import com.bist.zeromq.config.CommandCode;
import com.bist.zeromq.model.ZeroPeerRoutingInfo;
import com.bist.zeromq.model.internal.ProcessInfo;
import com.bist.zeromq.model.internal.RoutingTable;
import com.bist.zeromq.model.transfer.Command;
import com.bist.zeromq.model.transfer.Query;
import com.bist.zeromq.service.AnswerService;
import com.bist.zeromq.service.CommandService;
import com.bist.zeromq.service.QueryService;
import com.bist.zeromq.utils.ConnectionUtils;
import com.bist.zeromq.utils.ReportWriter;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


public class PeerStreamHandler extends Thread
{
    private final String inProcPath;
    private final ZeroPeerRoutingInfo zeroPeerRoutingInfo;
    private final ReportWriter reportWriter;
    private final ZContext context;
    private ZMQ.Socket inProcsocket;


    public PeerStreamHandler(ZContext context, String inProcPath, ZeroPeerRoutingInfo zeroPeerRoutingInfo,
        ReportWriter reportWriter)
    {
        this.context = context;
        this.zeroPeerRoutingInfo = zeroPeerRoutingInfo;
        this.reportWriter = reportWriter;
        this.inProcPath = inProcPath;
    }


    @Override
    public void run()
    {
        try
        {
            reportWriter.printf("Thread started with inProcPath %s \n", this.inProcPath);
            inProcsocket = context.createSocket(SocketType.PAIR);
            inProcsocket.bind(ConnectionUtils.inproc(inProcPath));

            inProcsocket.send(AnswerService.getOKMessage());

            reportWriter.println("Starting receiving in stream handler");
            while (!Thread.currentThread().isInterrupted())
            {
               // reportWriter.println("Items pooled");
                byte[] message = inProcsocket.recv(0);
                reportWriter.println("Handle stream requesting!");
                handleStream(message);
                //inProcsocket.send(AnswerService.getOKMessage());

            }

        }
        catch (Exception e)
        {
            reportWriter.println("Thread exception" + e.getLocalizedMessage());
        }
        finally
        {

            if (this.inProcsocket != null)
            {
                this.inProcsocket.close();
            }
            //this.join();

        }
    }

    public void handleStream(byte[]  stream)
    {
      Query query=  Query.decodedForm(stream);
      reportWriter.println("Readed query is" + query.toString());
      ZMQ.Socket socket= zeroPeerRoutingInfo.getStreamSocket(query,context);
      socket.send(stream);
      reportWriter.println("Readed stream" + query.toString());
      byte[]  reply=socket.recv();
      inProcsocket.send(reply);
      reportWriter.println("Stream directed to main thread");


    }

}
