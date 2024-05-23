package core.appium;

import UI.Controllers.MainController;
import core.MyLogger;
import core.managers.FileManager;
import core.managers.HostManager;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.net.URL;


/**
 * Contains an instance of the Appium server. Each server will be tied to a specific port on the PC and a specific
 * Android device.
 */
public class AppiumServer
{
    private final String defaultIpAddress = "127.0.0.1";
    private final int defaultPort = 4723;
    private final String basePath = "/wd/hub";
    private final int maxNumOfRetries = 2;


    private String instanceID = null;
    private String deviceID = null;
    private AppiumDriverLocalService service = null;


    /**
     * Constructor that requires the device ID (or any unique ID really) to append to the appropriate Appium log file
     * as each Appium server instance will write to its own log file.
     * @author Victor Dang
     */
    public AppiumServer(String instanceID)
    {
        this.instanceID = instanceID;
    }

    /**
     * Returns the instance ID assigned to this server.
     * @return The unique instance ID for this server.
     * @author Victor Dang
     */
    public String getInstanceID() { return instanceID; }

    /**
     * Gets the device ID of the device that this server is connected to. Can be used for device-server validation
     * or for some other purpose. This will be null if there are no devices connected to this server.
     * @return The string of the device ID that this server is connected to.
     * @author Victor Dang
     */
    public String getConnectedDeviceID()
    {
        return deviceID;
    }

    /**
     * Does this Appium server have any devices connected to it?
     * @return True if there is a device connected to it.
     * @author Victor Dang
     */
    public boolean hasDeviceConnected() { return deviceID != null; }

    /**
     * Removes deviceID to signify that this server no longer has a device connected to it.
     * @author Victor Dang
     */
    public void resetDeviceConnection() { deviceID = null; }

    /**
     * Starts up the Appium command line server. If the server does not exist, then this will create one and
     * then start it. There is no effect on calling this method when there is already an Appium server running.
     * @author Victor Dang
     */
    public void startServer()
    {
        if (isRunning())
        {
            MyLogger.log.debug("Appium server for " + getInstanceID() + " is already running!");
            return;
        }
        else if (service != null)
        {
            MyLogger.log.debug("Server instance " + getInstanceID() + " already exists, restarting it...");
            service.start();
            return;
        }


        boolean serverCreated = false;
        int retries = 0;

        do
        {
            try
            {
                HashMap<String, String> environment = new HashMap<>();
                environment.put("JAVA_HOME", HostManager.getJavaHome());
                environment.put("ANDROID_HOME", HostManager.getAndroidHome());

                AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
                serviceBuilder.withIPAddress(defaultIpAddress)
                        .usingAnyFreePort()
                        .withAppiumJS(FileManager.getAppiumLocation())
                        .usingDriverExecutable(FileManager.getNodeLocation())
                        .withArgument(GeneralServerFlag.BASEPATH, basePath)
                        .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                        .withArgument(GeneralServerFlag.ALLOW_INSECURE, "chromedriver_autodownload")
                        .withArgument(GeneralServerFlag.LOG_LEVEL, "debug:debug")
                        .withEnvironment(environment)
                        .withLogFile(FileManager.checkAndCreateFile(String.format(FileManager.getCurrentWorkingDirectory()
                                + "/" + FileManager.appiumLogs, getInstanceID())).toFile());

                service = AppiumDriverLocalService.buildService(serviceBuilder);
                MyLogger.log.debug("Server instance has been created");

                service.start();
                serverCreated = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ++retries;
            }
        } while (!serverCreated && retries <= maxNumOfRetries);
    }

    /**
     * Stops the server, but still maintaining the instance. Will do nothing if the service instance
     * does not exist. Use forceStop to stop the server even if there is a device connected.
     * @param forceStop Set to true to stop the server regardless of its connection status.
     * @author Victor Dang
     */
    public void stopServer(boolean forceStop)
    {
        if (service == null)
        {
            MyLogger.log.debug("No server was started!");
            return;
        }

        if (!hasDeviceConnected() || forceStop)
        {
            deviceID = null;
            MyLogger.log.debug("Stopped server {}", instanceID);
            service.stop();
        }
    }

    /**
     * Checks whether the server is still running or not.
     * @return The server status as a boolean, will return false if the service object is null.
     * @author Victor Dang
     */
    public boolean isRunning()
    {
        boolean running = (service != null && service.isRunning());
        MyLogger.log.debug("Server running? " + running);
        return running;
    }

    /**
     * Returns a formatted URL object using the default Appium server details. Always use this method to
     * connect to the internal Appium server as it will return the correct IP address and port that the
     * server is listening on. The deviceID passed to this method will be assumed to be the one that is connecting
     * to this server. deviceID will be overwritten each time this method is called. If the internal Appium server
     * could not be created for any reason, the default URL will be returned (<a href="http://127.0.0.1:4723">
     * http://127.0.0.1:4723</a>), this URL can be used for connecting to an external Appium server (though, this
     * is not recommended).
     * @param deviceID The ID of the device that will be connecting to this server.
     * @return Returns the formatted URL.
     * @author Victor Dang
     */
    public URL getAppiumURL(String deviceID)
    {
        if (service == null)
        {
            try
            {
                MyLogger.log.debug("Service is null, returning default URL");
                return new URL(defaultIpAddress + ":" + defaultPort + "/" + basePath);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            MyLogger.log.debug("URL string used for device {} = {}", deviceID, service.getUrl());
            this.deviceID = deviceID;
            return service.getUrl();
        }
    }
}
