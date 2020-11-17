package com.bist.zeromq.utils;

import java.io.FileNotFoundException;
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



}
