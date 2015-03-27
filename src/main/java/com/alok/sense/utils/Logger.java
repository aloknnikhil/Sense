package com.alok.sense.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Alok on 3/26/2015.
 */
public class Logger {

    private final String TAG = "Logger";
    private Context context;
    private File logDir;
    private File sessionLogFile;
    private PrintWriter sessionPrintWriter;
    public static float timeCounter;
    private Thread timeThread;

    public Logger(Context context)  {
        this.context = context;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            logDir = new File(android.os.Environment.getExternalStorageDirectory(),"SenseLogs");
        else
            logDir = context.getCacheDir();
        if(!logDir.exists())
            logDir.mkdirs();

        timeCounter = 0;

        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        timeCounter += 0.100f;
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
    }

    public void initializeLoggingSession() {
        long currentTime = System.currentTimeMillis();

        sessionLogFile = new File(logDir, currentTime + "");
        try {
            sessionPrintWriter = new PrintWriter(new FileWriter(sessionLogFile));
        } catch (IOException e) {
            Log.d(TAG, "PrintWriter failed to open log file");
        }

        timeThread.start();
    }

    public void terminateLoggingSession()   {
        if(sessionLogFile != null)  {
            if(sessionPrintWriter != null)  {
                sessionPrintWriter.flush();
                sessionPrintWriter.close();
                sessionPrintWriter = null;
            }
            sessionLogFile = null;
        }
        timeCounter = 0;
        timeThread.interrupt();
    }

    public void writeToLog(String logMessage)   {
        String enhancedMessage = "";

        enhancedMessage += "[Time: " + timeCounter + "] ";
        enhancedMessage += logMessage;
        sessionPrintWriter.println(enhancedMessage);
        Log.d(TAG, enhancedMessage);
    }
}
