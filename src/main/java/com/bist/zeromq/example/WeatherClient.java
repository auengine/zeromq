package com.bist.zeromq.example;

import com.bist.zeromq.config.Configuration;
import com.bist.zeromq.utils.GeneralUtils;
import com.bist.zeromq.utils.ReportWriter;
import com.bist.zeromq.utils.ConnectionUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.StringTokenizer;
import java.util.UUID;

public class WeatherClient
{
    private static final int serverPort = Configuration.SERVER_COMMAND_PORT;
    private static final String zipCode = Configuration.PUBLISHER_ZIPCODE;

    private static ReportWriter reportWriter;
    private static ZContext context;
    private static String instanceName =   UUID.randomUUID().toString();

    public static void main(String[] args)
    {
        try
        {
            reportWriter = GeneralUtils.createReportFile(WeatherClient.class.getSimpleName());
            reportWriter.printf("Starting %s with name %s\n",  WeatherClient.class.getSimpleName(),instanceName);
            reportWriter.printf("Port: %d\n",  serverPort);
            context = new ZContext();

            //  Socket to talk to server
            reportWriter.println("Staring subscribing");
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect(ConnectionUtils.tcp("localhost",serverPort));

            //  Subscribe to zipcode, default is NYC, 10001
            subscriber.subscribe(zipCode.getBytes(ZMQ.CHARSET));

            //  Process 100 updates
            int update_nbr;
            long total_temp = 0;
            for (update_nbr = 0; update_nbr < 100; update_nbr++) {
                //  Use trim to remove the tailing '0' character
                String string = subscriber.recvStr(0).trim();
                reportWriter.printf("Received string %s\n", string);

                StringTokenizer sscanf = new StringTokenizer(string, " ");
                int zipcode = Integer.valueOf(sscanf.nextToken());
                int temperature = Integer.valueOf(sscanf.nextToken());
                int relhumidity = Integer.valueOf(sscanf.nextToken());
                reportWriter.printf("Temprature token %d\n", temperature);
                total_temp += temperature;
            }

            reportWriter.printf("Average temperature for zipCode '%s' was %d.",
                zipCode,
                (int)(total_temp / update_nbr));


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

