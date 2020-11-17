package com.bist.zeromq.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Configuration
{


    public static final int SERVER_COMMAND_PORT;
    public static final int SERVER_STREAM_PORT;
    public static final int PUBLISHER_SUBSCRIBE_PORT;
    public static final int PUBLISHER_COMMAND_PORT;
    public static final String SERVER_IP;
    public static final String PUBLISHER_IP;
    public static final String PUBLISHER_ZIPCODE;

    public static final int INSTANCE_ID;
    public static final String QEUERY_LIST;
    public static final String TRT_LIST;


    private static final String SERVER_COMMAND_PORT_PROP = "zeromq.config.server.port";
    private static final String SERVER_STREAM_PORT_PROP = "zeromq.config.server.stream.port";
    private static final String SERVER_IP_PROP = "zeromq.config.server.ip";
    private static final String PUBLISHER_COMMAND_PORT_PROP = "zeromq.config.publisher.server.port";
    // private static final String TRACKER_SERVER_IP_PROP = "zeromq.config.tracker.ip";
    private static final String PUBLISHER_SUBSCRIBE_PORT_PROP = "zeromq.config.publisher.port";
    private static final String PUBLISHER_IP_PROP = "zeromq.config.publisher.ip";
    //   public static final String TRACKER_SERVER_IP;
    private static final String PUBLISHER_ZIPCODE_PROP = "zeromq.config.publisher.zipcode";
    private static final String INSTANCE_ID_PROP = "zeromq.config.instance.id";
    private static final String QEUERY_LIST_PROP = "zeromq.config.query.list";
    private static final String TRT_LIST_PROP = "zeromq.config.trt.list";

    static
    {

        SERVER_COMMAND_PORT = Integer.getInteger(SERVER_COMMAND_PORT_PROP, 20123).shortValue();
        SERVER_STREAM_PORT = Integer.getInteger(SERVER_STREAM_PORT_PROP, 20124).shortValue();
        PUBLISHER_SUBSCRIBE_PORT = Integer.getInteger(PUBLISHER_SUBSCRIBE_PORT_PROP, 20321).shortValue();
        PUBLISHER_COMMAND_PORT = Integer.getInteger(PUBLISHER_COMMAND_PORT_PROP, 20123).shortValue();
        SERVER_IP = System.getProperty(SERVER_IP_PROP, "127.0.0.1");
        //  TRACKER_SERVER_IP = System.getProperty(TRACKER_SERVER_IP_PROP, "127.0.0.1");
        PUBLISHER_IP = System.getProperty(PUBLISHER_IP_PROP, "127.0.0.1");
        PUBLISHER_ZIPCODE = System.getProperty(PUBLISHER_ZIPCODE_PROP, "10001");
        INSTANCE_ID = Integer.getInteger(INSTANCE_ID_PROP, 1);
        QEUERY_LIST = System.getProperty(QEUERY_LIST_PROP, "0|6");
        TRT_LIST = System.getProperty(TRT_LIST_PROP, "1|7");

    }


    public static List<Integer> getSortedProperties(final String input)
    {

        final List<Integer> result = new ArrayList<>();

        for (final String token : input.split("\\|"))
        {
            final String trimmed = token.trim();

            if (trimmed.isEmpty())
            {
                continue;
            }

            result.add(Integer.parseInt(trimmed));
        }

        Collections.sort(result);

        return result;
    }
}
