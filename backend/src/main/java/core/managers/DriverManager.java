package core.managers;

import UI.Controllers.MainController;
import org.testng.xml.XmlTest;
import api.Android;
import core.ADB;
import core.MyLogger;
import core.appium.AppiumServer;

import core.constants.AppInformation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.*;


/**
 * This class Manages the Driver created to run with the Appium Server.
 * The driver is required to receive commands from the Appium Server and sends these requests to the physical android
 * device.
 * @author Hayden Brehm
 */
public class DriverManager
{
    private static int currentAndroidIndex = 0;
    private final static ArrayList<Android> androidDevices = new ArrayList<>();


    private final static Object serverLock = new Object();
    private static CountDownLatch serverLatch;
    private final static ArrayList<AppiumServer> servers = new ArrayList<>();


    private static ScheduledExecutorService executor;
    private static ScheduledFuture<?> futureGetDevices;
    private static ArrayList<String> devicesSnapshot;


    /**
     * Starts a continuous background thread that will get all the devices currently connected
     * to the PC. This will find the connected devices through ADB periodically and updates the UI only when
     * the number of devices are changed. To stop polling for devices, use {@link DriverManager#stopGetDevices()}.
     * @author Victor Dang
     */
    public static void startGetDevices()
    {
        executor = Executors.newSingleThreadScheduledExecutor();
        futureGetDevices = executor.scheduleAtFixedRate(() ->
        {
            MyLogger.log.debug("Checking for connected devices");
            ArrayList<String> temp = ADB.getConnectedDevices();

            if (devicesSnapshot == null || devicesSnapshot.isEmpty() || devicesSnapshot.size() != temp.size())
            {
                // call on UI to update with device information
                MyLogger.log.debug("Updating UI with new connected devices");
                devicesSnapshot = temp;

                MyLogger.log.debug("devices connected = {} | server count = {}", temp.size(), servers.size());
            }
            else // both connected devices and snapshot of connected devices are the same number
            {
                boolean devicesChanged = false;
                for (String d : temp)
                {
                    if (!devicesSnapshot.contains(d))
                    {
                        devicesChanged = true;
                        break;
                    }
                }

                if (devicesChanged)
                {
                    // call on UI to update with device information
                    MyLogger.log.debug("Updating UI with new connected devices");
                    devicesSnapshot = temp;
                }
            }

            // only start up Appium servers when there is an increase in device connections, don't allow
            // Appium servers to be shutdown here, which will occur if temp.size() < server.size().
            if (temp.size() > servers.size())
                updateAppiumServers(false);
        }, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * Stops polling for connected devices, call {@link DriverManager#startGetDevices()} if devices need to be
     * polled every so often again.
     * @author Victor Dang
     */
    public static void stopGetDevices()
    {
        try
        {
            if (futureGetDevices != null)
            {
                MyLogger.log.debug("Canceling getDevices thread");
                futureGetDevices.cancel(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (executor != null)
            {
                MyLogger.log.debug("Shutting down getDevices executorService");
                executor.shutdownNow();
            }

            futureGetDevices = null;
            executor = null;
        }
    }

    /**
     * Gets all available devices.
     * These devices must be connected with an usb cable, and they must have usb debugging enabled in
     * developer settings on the device.
     * @throws RuntimeException No Devices are available. Can occur when nothing in connected by USB cable or
     * usb debugging is not enabled.
     * @return an ArrayList of all available devices that currently have an eligible apk installed.
     * @author Hayden Brehm, Victor Dang
     */
    public static ArrayList<String> getDevices(){
        ArrayList<String> availableDevices = new ArrayList<>();
        ArrayList<String> connectedDevices = ADB.getConnectedDevices();

        for (String device : connectedDevices)
        {
            MyLogger.log.debug("Connected device found: {}", device);
            ArrayList<String> apps = new ADB(device).getInstalledPackages();

            try
            {
                if (AppInformation.checkForSelectedPackage(apps))
                {
                    availableDevices.add(device);
                    MyLogger.log.debug("{} added to availableDevices", device);
                    MyLogger.log.debug("Package Exists!");
                }
                else
                {
                    MyLogger.log.debug("Package Doesn't Exist!");
                    throw new RuntimeException("Mismatch selected carrier and client package!");
                }
            }
            catch (RuntimeException ex)
            {
                MyLogger.log.debug("Runtime Exception! ----> {}", ex.getMessage());
                ex.printStackTrace();
            }
        }

        if (availableDevices.isEmpty())
            throw new RuntimeException("No devices available");

        return availableDevices;
    }

    /**
     * Creates the Driver to be used with the Appium Server.
     * This driver takes commands from the appium server and sends the request to the physical android device.
     * The driver's implicit timeout is set to 1 minute by default. ADB is instantiated with this device as its target.
     * This method is found in:
     * {@link tests.BaseTest#setup(XmlTest)}
     *
     * This method needs to be called sparingly and only at appropriate times because all existing drivers will be
     * removed and recreated (due to how the drivers are implemented), which will take a lot of time to create a
     * driver as it will go through the whole process of installing prequisite packages, setting capabilities, etc.
     * Driver creation will be threaded to allow parallel driver creation but the main thread will be blocked until
     * all device drivers are created completely.
     * @author Hayden Brehm, Victor Dang
     */
    public static void createDrivers() {
        ArrayList<String> devices = getDevices();
        MyLogger.log.debug("Num of devices returned from getDevices() -> {}", devices.size());

        // kill all existing drivers to clear out the devices array
        killAllDrivers();

        // Creates a driver for each device that doesn't already have a driver created. On each driver creation, the
        // driver will grab an Appium server URL (a server should have already been created when the device is plugged
        // in). Calling this function multiple times will not have any adverse effects, it will create drivers when
        // needed and will remove unused drivers or devices. Each Appium server will have its own unique port that
        // these drivers will connect to.

        // this countdown latch is used to check if there are any devices still being created.
        CountDownLatch deviceLatch = new CountDownLatch(devices.size());
        for (String deviceID : devices)
        {
            MyLogger.log.debug("Adding device with ID " + deviceID);

            new Thread(() -> {
                try
                {
                    URL url = getAvailableAppiumServer(deviceID); // synchronized method call
                    androidDevices.add(new Android(deviceID, url)); // this is a blocking call
                    deviceLatch.countDown();
                    MyLogger.log.debug("Device latch called, remaining = {}", deviceLatch.getCount());
                }
                catch (Exception e)
                {
                    MyLogger.log.debug("Issue with starting up {}", deviceID);
                    e.printStackTrace();
                    deviceLatch.countDown(); // countdown anyway even if there is an issue
                }
            }).start();
        }

        try
        {
            // block main thread until all device creation threads are completed
            MyLogger.log.debug("Waiting until all devices setup are complete");
            deviceLatch.await();
            MyLogger.log.debug("All devices have been set up");
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    /**
     * Instantiates a set number of server equal to the amount of devices connected. This should
     * be called once at the start of a test to remove any unused servers and called each time whenever a new
     * device is plugged in and there are not enough servers for the amount of devices plugged in. Each server
     * instance creation will be threaded so that the UI will not be blocked. The main thread will not be blocked
     * in this function under normal circumstances (if testStarted = false), but the test cannot be started from
     * the UI until all servers are started.
     * @param testStarted Pass true to indicate the test has started, this will block the UI until the servers are
     *      fully started. This is only used when there is a need to create servers.
     * @author Victor Dang
     */
    public static void updateAppiumServers(boolean testStarted)
    {
        // positive value = not enough servers
        // negative value = too many servers
        int serversNeeded = ADB.getConnectedDevicesCount() - servers.size();

        if (serversNeeded > 0)
        {
            serverLatch = new CountDownLatch(serversNeeded);
            MyLogger.log.debug("Number of servers needed " + serversNeeded);
            // notify UI to disable run button when entering this if block

            for (int i = 0; i < serversNeeded; ++i)
            {
                new Thread(() -> {
                    AppiumServer s = null;

                    try
                    {
                        synchronized (serverLock)
                        {
                            // synchronized to ensure that each instance of the server will have
                            // a unique instance ID
                            s = new AppiumServer("Instance_" + (servers.size() + 1));
                            servers.add(s);
                        }

                        s.startServer(); // startServer() is a blocking call
                        serverLatch.countDown();
                        MyLogger.log.debug("Server latch called, remaining = " + serverLatch.getCount());
                    }
                    catch (Exception e)
                    {
                        MyLogger.log.debug("Issue with starting up Appium server");
                        e.printStackTrace();
                        servers.remove(s);
                        serverLatch.countDown();
                    }
                }).start();
            }

            if (testStarted)
            {
                // when the test has been started, the main thread needs to be blocked until the servers are all
                // started up
                try
                {
                    MyLogger.log.debug("Blocking main thread until all servers have started up.");
                    serverLatch.await();
                    MyLogger.log.debug("All servers have been initialized, continuing with test");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                // threading this since server starts up on a different process but shouldn't block the main thread
                // only needed to update the UI by enabling the run button when all servers are started up
                new Thread(() -> {
                    try
                    {
                        MyLogger.log.debug("Waiting for servers to start up");
                        serverLatch.await();
                        MyLogger.log.debug("All servers have been initialized");
                        // notify UI to enable run button when all servers have started up
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace();
                    }
                }).start();
            }
        }
        else if (serversNeeded < 0)
        {
            serversNeeded = Math.abs(serversNeeded);
            MyLogger.log.debug("Number of servers to delete " + serversNeeded);

            // stop non-connected server instances, using while loops because we need to remove items
            // from the list
            int index = 0;
            int deleted = 0;
            while (index < servers.size() && deleted < serversNeeded)
            {
                // don't increment index when the server is being removed
                if (!servers.get(index).hasDeviceConnected())
                {
                    AppiumServer s = servers.remove(index);
                    MyLogger.log.debug("Removing server {}", s.getInstanceID());
                    s.stopServer(true);
                    ++deleted;
                }
                else
                {
                    ++index;
                }
            }
        }
    }

    /**
     * Closes any potential lingering Appium console servers. This is done by checking whether there are
     * any java or node-js processes running.
     * @author Victor Dang
     */
    public static void checkForAppiumProcesses()
    {
        HostManager.killProcesses("node", "java");
    }

    /**
     * Check and kills any memInfo executables.
     * @author Victor Dang
     */
    public static void checkForMemInfoProcesses()
    {
        HostManager.killProcesses(FileManager.MemInfoEXE);
    }

    /**
     * Checks and kills any lingering scrcpy processes.
     * @author Victor Dang
     */
    public static void checkForScrcpyProcesses()
    {
        HostManager.killProcesses(FileManager.scrcpyEXE.split("/")[1]);
    }

    /**
     * Returns the URL of the first available server. This method is synchronized, so only one thread can
     * access this at a time.
     * @return the URL of an available server or null if none are available.
     * @author Victor Dang
     */
    public static synchronized URL getAvailableAppiumServer(String deviceID)
    {
        if (!servers.isEmpty())
        {
            for (AppiumServer s : servers)
            {
                if (s != null && !s.hasDeviceConnected())
                    return s.getAppiumURL(deviceID);
            }
        }

        return null;
    }

    /**
     * Returns the Appium server instance for the specified device ID.
     * @param deviceID The ID of the device that is connected to a server instance.
     * @return The connected server instance, otherwise null.
     * @author Victor Dang
     */
    public static AppiumServer getConnectedAppiumServer(String deviceID)
    {
        if (!servers.isEmpty())
        {
            for (AppiumServer s : servers)
            {
                if (s != null && s.hasDeviceConnected() && s.getConnectedDeviceID().equals(deviceID))
                    return s;
            }
        }

        return null;
    }

    /**
     * Stops all the Appium server instances.
     * @author Victor Dang
     */
    public static void stopAllAppiumServers(boolean forceStop)
    {
        MyLogger.log.debug("Stopping all Appium servers");
        for (AppiumServer server : servers)
        {
            if (server != null)
                server.stopServer(forceStop);
        }

        checkForAppiumProcesses();
        servers.clear();
    }

    /**
     * Call to stop the driver for every connected device. This will NOT stop any Appium servers. Calling multiple
     * time will not have any adverse effects. This will also remove all devices from the devices array.
     * @author Victor Dang
     */
    public static void killAllDrivers()
    {
        MyLogger.log.debug("Kill drivers for all connected devices");
        for (Android android : androidDevices)
        {
            if (android != null)
                killDriver(android);
        }

        checkForScrcpyProcesses();
        androidDevices.clear();
    }

    /**
     * Kills the specified driver, disconnects it from any connected Appium server and removes it from the android
     * devices array.
     * @param android The device to kill.
     * @author Victor Dang
     */
    public static void killDriver(Android android)
    {
        MyLogger.log.debug("Checking to kill driver for {}", android.getDeviceID());
        AppiumServer s = getConnectedAppiumServer(android.getDeviceID());
        if (s != null)
            s.resetDeviceConnection();

        android.killDriver();
    }

    /**
     * Call to stop the driver and Appium server for every connected device. This should only be called in the
     * shutdown hook of this program.
     * @author Victor Dang
     */
    public static void shutdownAllDevices()
    {
        MyLogger.log.debug("Going through shutdown process for all devices");
        killAllDrivers();

        MyLogger.log.debug("Stopping all Appium servers");
        stopAllAppiumServers(true);

        MyLogger.log.debug("Stopping getDevices thread");
        stopGetDevices();

        MyLogger.log.debug("Ending MemInfoExecution processes...");
        checkForMemInfoProcesses();
        checkForAppiumProcesses();
        checkForScrcpyProcesses();
    }

    /**
     * Gets the driver belonging to the main target device
     * @return the default driver used in the main target device to execute tests
     * @author Hayden Brehm
     */
    public static Android getMainAndroid(){ return (androidDevices.isEmpty()) ? null : androidDevices.get(0); }

    /**
     * Sets the android device to use.
     * @param targetDevice The android device to use.
     * @author Victor Dang
     */
    public static void setCurrentAndroid(Android targetDevice)
    {
        for (int i = 0; i < androidDevices.size(); ++i)
        {
            if (androidDevices.get(i) == targetDevice)
            {
                currentAndroidIndex = i;
                break;
            }
        }
    }

    /**
     * Gets an android device at the specified index. This will not set the current android device. This will also clamp
     * the index between 0 and androidDevices.size() - 1, this will never throw an out-of-bounds exception but the
     * results may or may not be intended.
     * @param targetIndex The index of the device to use.
     * @return The android object or null if there are no devices in androidDevices.
     * @author Victor Dang
     */
    public static Android getAndroidAt(int targetIndex)
    {
        targetIndex = GetClampedIndex(targetIndex);
        return (targetIndex == -1) ? null : androidDevices.get(targetIndex);
    }

    /**
     * Sets the android device to use. This will also clamp the index value.
     * @param targetIndex The index of the android device to use.
     * @author Victor Dang
     */
    public static void setCurrentAndroid(int targetIndex)
    {
        targetIndex = GetClampedIndex(targetIndex);

        if (targetIndex != -1)
            currentAndroidIndex = targetIndex;
    }

    /**
     * Gets the currently selected android device.
     * @return The currently selected android device.
     * @author Hayden Brehm
     */
    public static Android getCurrentAndroid() { return androidDevices.get(currentAndroidIndex); }

    /**
     * Gets the current selected android index.
     * @return The current android index.
     * @author Victor Dang
     */
    public static int getCurrentAndroidIndex() { return currentAndroidIndex; }

    /**
     * Gets a device that is not the currently selected android device.
     * @return The target android.
     * @author Victor Dang
     */
    public static Android getTargetDevice()
    {
        if (currentAndroidIndex < androidDevices.size() - 1)
            return androidDevices.get(currentAndroidIndex + 1);
        else if (currentAndroidIndex > 0)
            return androidDevices.get(currentAndroidIndex - 1);

        return null;
    }

    /**
     * Gets the number of devices from the androidDevices ArrayList
     * @return the number of devices.
     * @author Hayden Brehm
     */
    public static int getAndroidDevicesCount() { return androidDevices.size(); }

    /**
     * Gets all the android devices.
     * @return all android devices.
     * @author Hayden Brehm
     */
    public static ArrayList<Android> getAllAndroidDevices() { return androidDevices; }

    /**
     * Receives an index and ensures that it is in bounds of the android devices array.
     * @param index The index to check
     * @return The clamped index or -1 if the androidDevices array is empty.
     * @author Victor Dang
     */
    private static int GetClampedIndex(int index)
    {
        if (androidDevices.isEmpty())
            return -1;
        else if (index < 0)
            return 0;
        else if (index >= androidDevices.size())
            return androidDevices.size() - 1;
        else
            return index;
    }
}
