package com.bist.zeromq.utils;

import com.bist.zeromq.config.MessageSize;
import com.bist.zeromq.config.MessageType;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils
{


    public static  ReportWriter createReportFile(String label) throws FileNotFoundException
    {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        final String reportFilename = String.format("log_"+label+ "_%s.txt",
            simpleDateFormat.format(new Date()));
        return new ReportWriter(reportFilename,true);

    }


    public static  String createBasePath(MessageType messageType, MessageSize messageSize,int totalClientCount,int totalMessageTypeCount,int instance) throws FileNotFoundException
    {
        final SimpleDateFormat simpleDayMMFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        String tmp="";
        if(isWindows()){
             tmp=System.getProperty("java.io.tmpdir");
             String file = "ZERO-"+ messageType.name() +"-" + messageSize.name() + "-%s-"+ "L" + instance+ ".dump";

             return tmp + "\\" +  String.format(file, simpleDateTimeFormat.format(new Date()));
        }else{
           tmp=  String.format("/tmp/zeromq/%s", simpleDayMMFormat.format(new Date()));
            String folder="ZERO-"+ (messageType.isQuery() ? "Q-":"T-" ) +totalClientCount +"-" + totalMessageTypeCount ;
            if(messageType.isQuery()){
                folder += "-"+ size(messageSize,totalClientCount);
            }
            String file = "ZERO-"+ messageType.name() +"-" + size(messageSize,1) + "-1111-"+ "L" + instance+ ".dump";

            String directoryName = tmp + "/" + folder;
            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdirs();
            }

            return  directoryName +"/" + file;

        }

    }

     private static int kb= 1024;
     private static int mb= 1024* 1024;


    private static String size(MessageSize messageSize, int total ){
        DecimalFormat f= new DecimalFormat("#.#");
        double bytes= messageSize.getSize() * total;
        if(bytes>= mb){
            double n= bytes/mb;
           return f.format(n)+"mb".replace(',','.');
        }else{
            double n= bytes/kb;
            return  f.format(n)+"kb".replace(',','.');
        }

    }


    private static String OS = null;
    private static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }
    public static boolean isWindows()
    {

        OS = System.getProperty("os.name");
        return getOsName().startsWith("Windows");
    }






}
