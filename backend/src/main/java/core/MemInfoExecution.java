package core;

import core.managers.FileManager;

import java.util.concurrent.*;


/**
 * @deprecated Methods merged into PythonScriptExecution now.
 */
@Deprecated
public class MemInfoExecution
{
    static ScheduledExecutorService executor;
    static ScheduledFuture<?> futureTimer;


    /**
     * One time run of the MemInfo script.
     * @author Edgar Bermudez
     */
    public static void runMemInfoScript()
    {
        PythonScriptExecution.memInfoScript();
    }

    /**
     * One time run of the MemInfo script.
     * @param folderName the folderName of the folder to be used as output.
     * @author Edgar Bermudez
     */
    public static void runMemInfoScript(String folderName)
    {
        PythonScriptExecution.memInfoScript(folderName);
    }

    /**
     * One time run of the MemInfo script.
     * @param folderName the folderName of the folder to be used as output.
     * @param uuid the uuid of the device to be checked. Used for multidevice use cases
     * @author Edgar Bermudez
     */
    public static void runMemInfoScript(String folderName, String uuid)
    {
        PythonScriptExecution.memInfoScript(folderName, uuid);
    }

    /**
     * Periodically runs the MemInfo script.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer()
    {
        if (FileManager.checkIfExists(FileManager.getDependenciesFolder() + FileManager.MemInfoEXE))
        {
            executor = Executors.newSingleThreadScheduledExecutor();
            futureTimer = executor.scheduleAtFixedRate(() -> {
                MyLogger.log.debug("Running memInfo script");
                runMemInfoScript();
            }, 0, 5, TimeUnit.MINUTES);
        }

        /*timer = new Timer("MemInfoTimer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //Task
                PythonScriptExecution pythonScriptExecution = new PythonScriptExecution();
                pythonScriptExecution.MemInfoScript();
            }
        };

        //5 minutes in milliseconds = 300,000 milliseconds
        timer.scheduleAtFixedRate(timerTask, 0, 300000);*/
    }

    /**
     * Periodically runs the MemInfo script.
     * @param folderName the name of the folder for the output
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer(String folderName)
    {
        if (FileManager.checkIfExists(FileManager.getDependenciesFolder() + FileManager.MemInfoEXE))
        {
            executor = Executors.newSingleThreadScheduledExecutor();
            futureTimer = executor.scheduleAtFixedRate(() -> {
                MyLogger.log.debug("Running memInfo script");
                runMemInfoScript(folderName);
            }, 0, 5, TimeUnit.MINUTES);
        }

        /*timer = new Timer("MemInfoTimer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //Task
                PythonScriptExecution pythonScriptExecution = new PythonScriptExecution();
                pythonScriptExecution.MemInfoScript();
            }
        };

        //5 minutes in milliseconds = 300,000 milliseconds
        timer.scheduleAtFixedRate(timerTask, 0, 300000);*/
    }

    /**
     * Periodically runs the MemInfo script.
     * @param folderName the name of the folder for the output
     * @param uuid the uuid of the device to be recorded. Used for multidevice usecase.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer(String folderName, String uuid)
    {
        if (FileManager.checkIfExists(FileManager.getDependenciesFolder() + FileManager.MemInfoEXE))
        {
            executor = Executors.newSingleThreadScheduledExecutor();
            futureTimer = executor.scheduleAtFixedRate(() -> {
                MyLogger.log.debug("Running memInfo script");
                runMemInfoScript(folderName, uuid);
            }, 0, 5, TimeUnit.MINUTES);
        }

        /*timer = new Timer("MemInfoTimer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //Task
                PythonScriptExecution pythonScriptExecution = new PythonScriptExecution();
                pythonScriptExecution.MemInfoScript();
            }
        };

        //5 minutes in milliseconds = 300,000 milliseconds
        timer.scheduleAtFixedRate(timerTask, 0, 300000);*/
    }

    /**
     * stops the timer and performs one last memInfo script call.
     * This is to obtain end of all tests memory information.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void endMemInfoTimer()
    {
        if (!FileManager.checkIfExists(FileManager.logsFolder))
        {
            MyLogger.log.debug("No logs were recorded!");
            return;
        }
        else if (futureTimer == null)
        {
            MyLogger.log.debug("MemInfo was not running, running one instance of MemInfo");
            runMemInfoScript();
            return;
        }

        try
        {
            MyLogger.log.debug("Stopping memInfo executorService");
            futureTimer.cancel(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            MyLogger.log.debug("Shutting down memInfo executorService");
            executor.shutdownNow();
        }


        //timer.cancel();
        runMemInfoScript(); //Run python script
    }
}
