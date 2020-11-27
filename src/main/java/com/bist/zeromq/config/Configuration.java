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
    public static final String DEST_SERVER_IP;
    public static final String PUBLISHER_IP;
    public static final String PUBLISHER_ZIPCODE;

    public static final int INSTANCE_ID;
    public static final String QEUERY_LIST;
    public static final int MESSAGE_TYPE_ITEM;
    public static final int MESSAGE_SIZE_ITEM;
    public static final String TRT_LIST;

   // public static final int ANSWER_SIZE;
   // public static final int ANSWER_SEGMENT_SIZE;
    public static final int MESSAGE_COUNT;
    public static final int TOTAL_CLIENT_COUNT;
    public static final int TOTAL_MESSAGE_TYPE_COUNT;
    public static final String WAIT_TIME_TILL;








    private static final String SERVER_COMMAND_PORT_PROP = "zeromq.config.server.port";
    private static final String SERVER_STREAM_PORT_PROP = "zeromq.config.server.stream.port";
    private static final String SERVER_IP_PROP = "zeromq.config.server.ip";
    private static final String DEST_SERVER_IP_PROP = "zeromq.config.dest.server.ip";
    private static final String PUBLISHER_COMMAND_PORT_PROP = "zeromq.config.publisher.server.port";
    // private static final String TRACKER_SERVER_IP_PROP = "zeromq.config.tracker.ip";
    private static final String PUBLISHER_SUBSCRIBE_PORT_PROP = "zeromq.config.publisher.port";
    private static final String PUBLISHER_IP_PROP = "zeromq.config.publisher.ip";
    //   public static final String TRACKER_SERVER_IP;
    private static final String PUBLISHER_ZIPCODE_PROP = "zeromq.config.publisher.zipcode";
    private static final String INSTANCE_ID_PROP = "zeromq.config.instance.id";
    private static final String QEUERY_LIST_PROP = "zeromq.config.query.list";
    private static final String MESSAGE_TYPE_ITEM_PROP = "zeromq.config.message.item";
    private static final String MESSAGE_SIZE_ITEM_PROP = "zeromq.config.message.size";
    private static final String TRT_LIST_PROP = "zeromq.config.trt.list";
    //private static final String ANSWER_SIZE_PROP = "zeromq.config.answer.size";
    //private static final String ANSWER_SEGMENT_SIZE_PROP = "zeromq.config.answer.segment.size";
    private static final String MESSAGE_COUNT_PROP = "zeromq.config.message.count";
    private static final String TOTAL_CLIENT_COUNT_PROP = "zeromq.config.test.client.total.count";
    private static final String TOTAL_MESSAGE_TYPE_COUNT_PROP = "zeromq.config.test.message.type.count";
    private static final String WAIT_TIME_TILL_PROB = "zeromq.config.test.wait.time";

    static
    {

        SERVER_COMMAND_PORT = Integer.getInteger(SERVER_COMMAND_PORT_PROP, 20124).shortValue();
        SERVER_STREAM_PORT = Integer.getInteger(SERVER_STREAM_PORT_PROP, 20125).shortValue();
        PUBLISHER_SUBSCRIBE_PORT = Integer.getInteger(PUBLISHER_SUBSCRIBE_PORT_PROP, 20321).shortValue();
        PUBLISHER_COMMAND_PORT = Integer.getInteger(PUBLISHER_COMMAND_PORT_PROP, 20123).shortValue();
        SERVER_IP = System.getProperty(SERVER_IP_PROP, "127.0.0.1");
        DEST_SERVER_IP = System.getProperty(DEST_SERVER_IP_PROP, "127.0.0.1");
        //  TRACKER_SERVER_IP = System.getProperty(TRACKER_SERVER_IP_PROP, "127.0.0.1");
        PUBLISHER_IP = System.getProperty(PUBLISHER_IP_PROP, "127.0.0.1");
        PUBLISHER_ZIPCODE = System.getProperty(PUBLISHER_ZIPCODE_PROP, "10001");
        INSTANCE_ID = Integer.getInteger(INSTANCE_ID_PROP, 1);
        QEUERY_LIST = System.getProperty(QEUERY_LIST_PROP, "1");
        TRT_LIST = System.getProperty(TRT_LIST_PROP, "4");
       // ANSWER_SIZE = Integer.getInteger(ANSWER_SIZE_PROP, MessageSize.MB30.getSize());
        //ANSWER_SEGMENT_SIZE = Integer.getInteger(ANSWER_SEGMENT_SIZE_PROP, MessageSize.KB60.getSize());
        MESSAGE_COUNT = Integer.getInteger(MESSAGE_COUNT_PROP,1000);
        TOTAL_CLIENT_COUNT = Integer.getInteger(TOTAL_CLIENT_COUNT_PROP, 1);
        TOTAL_MESSAGE_TYPE_COUNT = Integer.getInteger(TOTAL_MESSAGE_TYPE_COUNT_PROP,1);
        MESSAGE_TYPE_ITEM = Integer.getInteger(MESSAGE_TYPE_ITEM_PROP, 1);
        MESSAGE_SIZE_ITEM = Integer.getInteger(MESSAGE_SIZE_ITEM_PROP, 1);
        WAIT_TIME_TILL = System.getProperty(WAIT_TIME_TILL_PROB, "");

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
