package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import com.bist.zeromq.utils.ConnectionUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;
import java.util.UUID;

public class WeatherPublisher
{
    private static final int serverPort = Configuration.SERVER_COMMAND_PORT;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName =   UUID.randomUUID().toString();

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(WeatherPublisher.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  WeatherPublisher.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();

            // Socket to publish  clients
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind(ConnectionUtils.tcpOld(serverPort));
           // publisher.bind(StringUtils.ipc("weather"));


            //  Initialize random number generator
            Random srandom = new Random(System.currentTimeMillis());
            while (!Thread.currentThread().isInterrupted()) {
                //  Get values that will fool the boss
                int zipcode, temperature, relhumidity;
                zipcode = 10000 + srandom.nextInt(10000);
                temperature = srandom.nextInt(215) - 80 + 1;
                relhumidity = srandom.nextInt(50) + 10 + 1;

                //  Send message to all subscribers
                String update = String.format(
                    "%05d %d %d", zipcode, temperature, relhumidity
                );
                reportWriter.println("Publishing: [" + update + "]");
                publisher.send(update, 0);
            }

        }
        catch (Exception e)
        {
            reportWriter.println("Exception occured:" + e.getLocalizedMessage());
        }
        finally
        {
            if (context != null)
            {
                context.close();
            }
            reportWriter.println("Finalizing application.");
        }
    }
}

