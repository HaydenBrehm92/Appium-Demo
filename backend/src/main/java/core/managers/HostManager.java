package core.managers;

import UI.Controllers.MainController;
import core.MyLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * This class involves android adb, System OS, and ensuring that adb commands are run.
 * @author Hayden Brehm
 */
public class HostManager
{
    /**
     * Finds the SDK folder from the current working directory first since the SDK should be packaged in the same
     * folder as this jar file. If there is not SDK folder in current directory, then look for ANDROID_HOME in
     * system environment.
     * @return The path to the SDK either in current directory or from system environment variable.
     * @author Hayden Brehm, Victor Dang
     */
    public static String getAndroidHome()
    {
        String sdkPath;
        String sdkDebug = FileManager.getDependenciesFolder() + FileManager.sdkFolder;

        if (MainController.DEBUG && FileManager.checkIfExists(sdkDebug))
        {
            sdkPath = sdkDebug;
        }
        else
        {
            sdkPath = (FileManager.checkIfExists(FileManager.adbEXE))
                ? FileManager.sdkFolder
                : System.getenv("ANDROID_HOME");
        }

        MyLogger.log.debug("Using SDK: {}", sdkPath);

        if (sdkPath != null)
            return sdkPath;
        else
            throw new RuntimeException("ANDROID_HOME or SDK not found!");
    }

    /**
     * Finds the JDK folder from the current working directory first since the tool will come packaged
     * with a JDK. If there is no JDK folder, then it will look for JAVA_HOME in the system environment.
     * @return The path to the JDK in either the current directory or from system environment variable.
     * @author Hayden Brehm, Victor Dang
     */
    public static String getJavaHome()
    {
        String jdkPath;
        String jdkDebug = FileManager.getDependenciesFolder() + FileManager.jdkFolder;

        if (MainController.DEBUG && FileManager.checkIfExists(jdkDebug))
        {
            jdkPath = jdkDebug;
        }
        else
        {
            jdkPath = (FileManager.checkIfExists(FileManager.javaExe))
                ? FileManager.jdkFolder
                : System.getenv("JAVA_HOME");
        }

        MyLogger.log.debug("Using JDK: {}", jdkPath);

        if (jdkPath != null)
            return jdkPath;
        else
            throw new RuntimeException("JAVA_HOME or JDK not found!");
    }

    /**
     * Gets the OS of the system that is to be used by {@link HostManager#isWindows()} and {@link HostManager#isMac()}.
     * @return returns the {@code OS} of the system running the .jar
     * @author Hayden Brehm
     */
    public static String getOS()
    {
        String os = System.getProperty("os.name");
        MyLogger.log.debug("Retrieved OS: {}", os);
        return os;
    }

    /**
     * Checks to see if the system is Windows
     * @return {@code boolean} with a value of true if the system environment name of "os.name" starts with Windows.
     * False otherwise.
     * @author Hayden Brehm
     *
     */
    public static boolean isWindows(){
        return getOS().startsWith("Windows");
    }

    /**
     * Checks to see if the system is Mac
     * @return {@code boolean} with a value of true if the system environment name of "os.name" starts with Mac.
     * False otherwise.
     * @author Hayden Brehm
     */
    public static boolean isMac(){
        return getOS().startsWith("Mac");
    }

    /**
     * Runs the provided console command using Scanner to execute the command. Delimiter \\A ensures that the entire
     * text returned from running the console command is stored in output. Used in {@link core.ADB#adbCommand(String)}.
     * @param command is the command to be executed.
     * @return String with the returned output from running the console command.
     * @author Hayden Brehm, Victor Dang
     */
    public static String executeConsoleCommandWithOutput(String command)
    {
        String output = null;

        try
        {
            Scanner scanner = new Scanner(executeConsoleCommand(command).getInputStream()).useDelimiter("\\A");
            output = (scanner.hasNext()) ? scanner.next() : null;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return output;
    }

    /**
     * Executes a console command. This will return a Process object to keep track of the running process.
     * @param command the command to be executed.
     * @return the process that was created, may be null if the process wasn't able to be started.
     * @author Victor Dang
     */
    public static Process executeConsoleCommand(String command)
    {
        Process p = null;

        try
        {
            MyLogger.log.debug("Process command to execute: {}", command);
            p = Runtime.getRuntime().exec(command);
            MyLogger.log.debug("Process successfully created? {}", (p != null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return p;
    }

    /**
     * Kills all processes that contain the specified strings.
     * @param processNames The name(s) of the processes to find, separated by commas.
     * @author Victor Dang
     */
    public static void killProcesses(String... processNames)
    {
        try
        {
            ArrayList<String> instances = new ArrayList<>();
            Process p = Runtime.getRuntime().exec("tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // getting the PID for this java process so it doesn't get killed
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //MyLogger.log.debug("PID = {}", pid);

            String line;
            while ((line = input.readLine()) != null)
            {
                //MyLogger.log.debug(line);
                String[] tasks = line.split("[ ]+", -1);
                for (String name : processNames)
                {
                    //MyLogger.log.debug("Checking for {} processes to kill", name);
                    if (line.contains(name) && !tasks[1].equals(pid) && !(MainController.DEBUG && line.contains("java")))
                    {
                        Runtime.getRuntime().exec("taskkill /F /PID " + tasks[1]);
                        MyLogger.log.debug("Killing {} with process ID {}", tasks[0], tasks[1]);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
