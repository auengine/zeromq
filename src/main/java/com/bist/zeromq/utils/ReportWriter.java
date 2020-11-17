package com.bist.zeromq.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ReportWriter
{

    private final PrintStream fileStream;
    private final boolean consoleOutput;

    public ReportWriter(final String filename, final boolean consoleOutput) throws FileNotFoundException
    {
        this.consoleOutput = consoleOutput;

        fileStream = new PrintStream(new FileOutputStream(filename));
    }

    public void close()
    {
        try
        {
            fileStream.close();
        }
        catch (final Exception e)
        {
            System.out.println("Error on closing file " + e.getMessage());
        }
    }

    public void println(final String string)
    {

        fileStream.println(string);

        if (consoleOutput)
        {
            System.out.println(string);
        }
    }

    public void errorln(final String string)
    {
        println("[ERROR]" + string);
    }

    public void printf(final String format, final Object... args)
    {
        fileStream.printf(format, args);

        if (consoleOutput)
        {
            System.out.printf(format, args);
        }
    }

    public void errorf(final String format, final Object... args)
    {
        printf("[ERROR]" + format, args);
    }

    public PrintStream getFileStream()
    {
        return fileStream;
    }
}
