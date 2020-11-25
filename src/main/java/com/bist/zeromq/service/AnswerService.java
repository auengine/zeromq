package com.bist.zeromq.service;

import com.bist.zeromq.config.Constants;
import com.bist.zeromq.config.MessageSize;
import org.zeromq.ZFrame;
import org.zeromq.ZMsg;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AnswerService
{
    private static final byte[] ok = new byte[]{ 'O', 'K' };
    private static final byte[] dummy = new byte[]{ 'M', 'U', 'R', 'A', 'T', '!' };
    private static final Map<MessageSize, ZMsg> answerMap = new HashMap<>();
    private static final byte[] answer = ByteBuffer.allocate(Constants.MAX_ANSWER_SIZE).array();
    private static boolean init = false;

    private static void init()
    {
        int j = 0;
        for (int i = 0; i < answer.length; i++, j++)
        {
            answer[i] = dummy[j];
            if (j == dummy.length - 1)
            {
                j = 0;
            }
        }
    }


    public static byte[] getAnswer()
    {
        if (!init)
        {
            init();
            init = true;
        }
        return answer;

    }

    public static ZMsg getAnswer(MessageSize size)
    {
        if (!answerMap.containsKey(size))
        {
            ZMsg msg = new ZMsg();
            ZFrame ans= new ZFrame(getAnswer());
            msg.add(ans);
            answerMap.put(size, msg);

        }
        return answerMap.get(size);
    }


    public static byte[] getOKMessage()
    {
        return ok;
    }

    public static byte[] getIntMessage(int i, ByteBuffer buffer)
    {
        buffer.putInt(0, i);
        return buffer.array();
    }

    public static int getIntMessage(byte[] bytes)
    {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return b.getInt(0);
    }


}
