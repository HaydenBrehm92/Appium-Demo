package core;

import core.managers.FileManager;
import core.managers.HostManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * This class executes the python executable after tests are run.
 * @author Hayden Brehm
 */
public class PythonScriptExecution
{
    private static Process dataBreakdownProcess;
    private static ScheduledExecutorService executor;
    private static ScheduledFuture<?> futureTimer;


    /**
     * Periodically runs the MemInfo script.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer()
    {
        startMemInfoTimer("", "");
    }

    /**
     * Periodically runs the MemInfo script.
     * @param folderName the name of the folder to be used as output location.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer(String folderName)
    {
        startMemInfoTimer(folderName, "");
    }

    /**
     * Periodically runs the MemInfo script.
     * @param folderName the name of the folder to be used as output location.
     * @param uuid the UUID of the device, if any. Used for the memInfo script when multiple devices are used.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void startMemInfoTimer(String folderName, String uuid)
    {
        if (FileManager.checkIfExists(FileManager.getDependenciesFolder() + FileManager.MemInfoEXE))
        {
            MyLogger.log.debug("Starting memInfo timer");
            executor = Executors.newSingleThreadScheduledExecutor();
            Runnable command = () -> PythonScriptExecution.memInfoScript(folderName, uuid);
            futureTimer = executor.scheduleAtFixedRate(command, 0, 5, TimeUnit.MINUTES);
        }
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
            memInfoScript();
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

        memInfoScript(); //Run python script
    }

    /**
     * Runs the python executable of DataBreakdown found in the working directory.
     * @author Hayden Brehm, Edgar Bermudez, Victor Dang
     */
    public static void dataBreakdownScript()
    {
        dataBreakdownScript("");
    }

    /**
     * Runs the python executable of DataBreakdown found in the working directory.
     * @param folderName name of the folder parameter of the python script, if any.
     * @author Hayden Brehm, Edgar Bermudez, Victor Dang
     */
    public static void dataBreakdownScript(String folderName)
    {
        MyLogger.log.debug("Running DataBreakdown script");

        if (dataBreakdownProcess != null)
        {
            MyLogger.log.debug("Stopping existing DataBreakdown process");
            dataBreakdownProcess.destroy();
        }

        dataBreakdownProcess = runScript(FileManager.DataBreakdownEXE, folderName, "");
    }

    /**
     * Runs the python executable of MemInfo found in the working directory.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void memInfoScript()
    {
        memInfoScript("", "");
    }

    /**
     * Runs the python executable of MemInfo found in the working directory.
     * @param folderName name of the folder parameter of the python script
     * @author Edgar Bermudez, Victor Dang
     */
    public static void memInfoScript(String folderName)
    {
        memInfoScript(folderName, "");
    }

    /**
     * Runs the python executable of MemInfo found in the working directory.
     * @param folderName name of the folder parameter of the python script
     * @param uuid the UUID of the device, if any. Used for the memInfo script when multiple devices are used.
     * @author Edgar Bermudez, Victor Dang
     */
    public static void memInfoScript(String folderName, String uuid)
    {
        MyLogger.log.debug("Running MemInfo script");
        MyLogger.log.debug("Checking for existing MemInfo processes");
        HostManager.killProcesses(FileManager.MemInfoEXE);
        runScript(FileManager.MemInfoEXE, folderName, uuid);
    }

    /**
     * Runs the python executable found in the working directory, based on the name provided.
     * @param scriptName name of the script file to run.
     * @param folderName name of the folder parameter of the python script, if any.
     * @param uuid the UUID of the device, if any. Used for the memInfo script when multiple devices are used.
     * @return The process to the running executable.
     * @author Edgar Bermudez, Victor Dang
     */
    public static Process runScript(String scriptName, String folderName, String uuid)
    {
        Process p = null;

        try
        {
            String path = FileManager.getDependenciesFolder() + scriptName;
            if (FileManager.checkIfExists(path))
            {
                p = Runtime.getRuntime().exec(new String[]{path, folderName, uuid});
                MyLogger.log.debug("Python script: " + scriptName + " is executing!");
            }
            else
            {
                MyLogger.log.debug("{} ----> Unknown script file, or .exe file does not exist! Can't execute provided file", scriptName);
            }
        }
        catch (Exception e)
        {
            MyLogger.log.debug("Exception Occurred with python script: {}! ----> {}", scriptName, e.getMessage());
            e.printStackTrace();

            if (p != null && p.isAlive())
            {
                p.destroy();
                p = null;
            }
        }

        return p;
    }
}